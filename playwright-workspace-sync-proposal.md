# Playwright in Liferay Workspaces: Shared Infra, Workspace-Owned Tests

Status: in progress on branch `workspace-playwright-migration`. The WIP commit `c171fc607952a` holds the original suite; the working tree holds the changes below, uncommitted. Updated 2026-10-07.

## Goal

Playwright infrastructure lives in every workspace. `liferay-sample-workspace` is its source of truth, and `workspaces/refresh_other_workspaces.sh` syncs it to every other workspace. Tests, fixtures, and helpers that are unique to a workspace live in one directory the sync never touches.

The concrete payoff is version checking. Bump `liferay.workspace.product`, run one command, and see whether the workspace's client extensions still work on that Liferay version.

## Layout

Everything is under `<workspace>/playwright`.

| Path | Owner | Synced |
| --- | --- | --- |
| `build.gradle`, `package.json`, `playwright.config.ts`, `tsconfig.json`, `global-setup.ts` | sample | yes |
| `fixtures/test.ts` | sample | yes |
| `helpers/` | sample | yes |
| `workspace/tests/` | each workspace | no |
| `workspace/fixtures/test.ts` | each workspace | no |
| `workspace/helpers/` | each workspace | no |
| `.env` | sample | yes |
| `.env.local` | developer | no, and ignored by Git |

There is no `playwright/yarn.lock`: the root `yarn.lock` owns the packages, because the workspace plugin makes `playwright` a root yarn workspace member.

Specs import `test` and `expect` from `../fixtures/test`, which is `workspace/fixtures/test.ts`. That file extends the shared test, so a workspace adds fixtures there without touching the shared layer.

Compose overrides follow the same split. `docker-compose.<name>.yaml` is shared and syncs. `docker-compose-<name>.yaml` belongs to the workspace, and the sync already excluded it. `scripts/_common.sh` includes both forms for the same override name, so a workspace adds a service its tests need in `docker-compose-functional-test.yaml`.

## Done

- [x] Anchor the sync's root-level excludes so nested infra files such as `playwright/build.gradle` sync. Verified that no other workspace has a nested `build.gradle`, `package.json`, `README.md`, `yarn.lock`, `gradle.properties`, `test.properties`, or `Jenkinsfile` outside an already excluded directory, so the anchoring changes nothing in them today.
- [x] Exclude `/playwright/workspace` as the single workspace-owned directory, plus `.env.local`, `.gradle`, `build`, `playwright-report`, and `test-results`. Before this, a sync from a built sample copied its `.gradle` and `build` directories into every workspace.
- [x] Move the sample's specs to `playwright/workspace/tests`, and its sample-shaped helpers (`customElements.ts`, `importMaps.ts`) to `playwright/workspace/helpers`. Add `playwright/workspace/fixtures/test.ts` that extends the shared test.
- [x] Make `playwright/build.gradle` independent of the root `build.gradle`: `node { global = false; nodeVersion = "22.23.3" }` downloads Node into `playwright/build/node`. The root `build.gradle` and `liferay-sample-custom-element-3/build.gradle` are back to master.
- [x] Run Playwright through yarn scripts (`PackageRunTask` with `scriptName`) instead of a hardcoded `cli.js` path. The workspace plugin's `setUpYarn` task rewrites the root `package.json` workspaces list from every `package.json` it finds, so `playwright` is always a member and `@playwright/test` is hoisted to the root `node_modules`. Yarn resolves the binary either way. Verified: `./gradlew :playwright:installPlaywrightBrowsers` and `./gradlew :playwright:testFunctional --dry-run` succeed.
- [x] Authenticate API calls as a separate administrator. `global-setup.ts` creates `playwright@liferay.com` once per run, as the browser's user, before any browser session exists. `LiferayAPI.create()` defaults to that user. This avoids the 2026.q3.5 behavior where a basic auth request as the browser's user signs the browser out.
- [x] One configuration model: `helpers/environment.ts` loads `playwright/.env.local`, then `playwright/.env`, then the environment, with later sources winning. `PORTAL_URL`, `LIFERAY_ADMIN_*`, and `LIFERAY_API_*` are documented in the README.
- [x] Rename `docker-compose-functional-test.yaml` to `docker-compose.functional-test.yaml` so it syncs. `docker-compose-microservices.yaml` stays workspace-owned, and the test scripts include it only when it exists.
- [x] Derive the microservice list from Compose instead of hardcoding `MICROSERVICE_NAMES`: every service the overrides add on top of `docker-compose.yaml`. Verified it yields `liferay-sample-etc-node` and `liferay-sample-etc-spring-boot`.
- [x] Document the shared/owned split, the Compose naming, and the configuration variables in the sample's README.
- [x] Type-check the suite with TypeScript 5.9 (`tsc --noEmit -p playwright`). The root's TypeScript 4.4 cannot parse the `@types/node` that Playwright hoists to the root `node_modules`, and `npx` picks that local 4.4 first, so install 5.9 somewhere else and call its `tsc` by path. Playwright transpiles with its own Babel, so this only affects the check, not the run.

## To Do

- [x] Run the full suite against the stack: 50 passed on 2026.q3.5 on 2026-10-21, headed, via `./gradlew testFunctional -Pplaywright.args="--headed"`. The API user fixed `liferay-sample-custom-element-4` and `liferay-sample-theme-css-2`. A run in between showed three `liferay-sample-etc-spring-boot` failures: redeploying a client extension gives its OAuth application a new client ID, and the running sidecar had read the old one at startup. `start.sh` now recreates the microservices after any redeploy. The recreate path was verified by hand with `docker compose up --force-recreate`; the Gradle run that followed found every zip already deployed, so it did not exercise it.
- [ ] Client extension zips rebuild with new checksums more often than their sources change, which triggers a redeploy and now a sidecar recreate. Check whether the zip build is reproducible, or compare on content instead.
- [ ] Run `workspaces/refresh_other_workspaces.sh` and review the result in a couple of workspaces. A dry run against `liferay-dev-workspace` showed only the expected additions: `playwright/` minus `workspace/`, `docker-compose.functional-test.yaml`, `scripts/functional_test/`, `scripts/microservices/`, and the updated `.gitignore` and `settings.gradle`. Check `liferay-portal-master-private/workspaces` too.
- [ ] Verify the suite in a second workspace. After the sync, add one spec under `playwright/workspace/tests` there and run `./gradlew testFunctional`. The `include "playwright"` in `settings.gradle` reaches every workspace through the sync; this is the same pattern as `include "poshi"`, which 26 workspaces carry while only 8 have a `poshi` directory.
- [ ] Decide what the AI Hub PR's harness contributes to the shared layer: user and account API helpers, data-tracking cleanup, `trackPendingApiRequests`, feature flags, and a login page object. The AI Hub page objects and MockMock page go to `playwright/workspace/` in `liferay-aihub-workspace`, and MockMock goes in its `docker-compose-functional-test.yaml`.
- [ ] Decide whether the `gradle.properties` bump from `dxp-2024.q2.8` to `dxp-2026.q3.5` stays on this branch or goes in its own commit. It is unrelated to the sync work, but the suite was only ever run on q3.5.
- [ ] Ask the portal team whether basic auth signing out the same user's browser session on 2026.q3.5 is intended. The API user sidesteps it, but other suites may hit it.
- [ ] The root `yarn install` under Node 16 fails to build `nice-napi` with Python 3.12 (`No module named 'distutils'`). It is an optional dependency, so the install still succeeds, but the noise hides real errors. This is pre-existing and unrelated to Playwright.
- [ ] Split the working tree into commits. Suggested order: sync script excludes; Playwright layout and Gradle wiring; API user and configuration; Compose naming and scripts; README.

## Reference

### The Driving PR

[brianchandotcom/liferay-portal-ee#51418](https://github.com/brianchandotcom/liferay-portal-ee/pull/51418), "LPD-102672 Migrate the AI Hub Playwright tests into the workspace", forwarded from `liferay-ai-hub/liferay-portal-ee#125`. Closed unmerged on 2026-10-07; Brian asked Davyson Melo to coordinate with Drew Brokke. It placed its suite under `modules/playwright` because the root-level location lost 32 of 34 files to the sync, and that location coupled the suite to `distBundle` through `ModulesProjectConfigurator`. The layout above resolves both problems.

### The Sync Script

`workspaces/refresh_other_workspaces.sh` runs `rsync --archive --delete` from `liferay-sample-workspace` to every other workspace in `liferay-portal` and `liferay-portal-master-private`. Patterns that start with `/` match only at the workspace root. Patterns without one match at any depth. `rsync --delete` never deletes an excluded path on the receiving side, which is what protects `playwright/workspace`.

### Gradle Plugin Behavior Worth Knowing

- `NodeExtension` downloads Node into the root project's `build/node` when `global` is true, which is the default. Two projects that pin different versions with `global` true overwrite each other.
- `LiferayWorkspaceYarnPlugin` sets `useNpm` false on every `NpmInstallTask` and finalizes each one with the root `yarnInstall`. `SetUpYarnTask` rewrites the root `package.json` workspaces list from every `package.json` under the workspace, in filesystem order, which is why that list reorders itself.
- Gradle tolerates an `include` whose directory does not exist, so a synced `settings.gradle` is safe in a workspace that has not received the directory yet.
