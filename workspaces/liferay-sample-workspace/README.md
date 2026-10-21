# Liferay Sample Workspace

The Liferay Sample workspace contains an example of every client extension and is meant to be the ***primary*** source of truth of how client extensions work by documenting via code.

Feel free to initiate your project by copying the samples found here. But make sure to rename your client extensions according to our established [naming conventions](#naming-conventions).

## Client Extensions

Client extensions are the recommended way of customizing Liferay. Modules and themes are supported for backwards compatibility.

To deploy all client extensions, go to `liferay-sample-workspace` and type `./gradlew deploy`.

To deploy a specific client extension (e.g. liferay-sample-custom-element-1), go to `liferay-sample-workspace` and type `./gradlew :client-extensions:liferay-sample-custom-element-1:deploy`.

### Naming Conventions

The standard directory name of a client extension is broken up into several parts. The first two parts, the owner and project, are separated by `-`.

For `liferay-sample-batch`, the owner is `liferay` and the project is `sample`. The owner and the project must not contain `-` since we use `-` to differentiate the owner from the project.

The third part is usually one of the available client extension types: batch, custom-element, fds-cell-renderer, global-css, global-js, iframe, notification-type, oahs, oaua, object-action, site-initializer, static-content, theme-css, theme-favicon, theme-spritemap, or workflow-action.

For `liferay-sample-batch`, the third part is the client extension type `batch`.

For `liferay-sample-custom-element-1` and `liferay-sample-custom-element-2`, the third part, `custom-element`, means this is a custom element. The fourth parts, `1` and `2`, are a general description that can be anything.

If the third part it is not a client extension type, then the third part must be the special keyword `etc`.

For `liferay-sample-etc-cron` and `liferay-sample-etc-spring-boot` the third type is `etc`. The fourth parts, `cron` and `spring-boot` are general descriptions that can be anything.

### List of Client Extensions

- *liferay-sample-audiences-custom-attributes*

	Add custom attributes to detect a visitor's audiences.

- *liferay-sample-batch*

	...

- *liferay-sample-commerce-checkout-step*

	Add a checkout step to the commerce checkout widget.

- *liferay-sample-commerce-payment-integration*

	TODO

- *liferay-sample-commerce-shipping-engine*

	Use Spring Boot to provide a new commerce shipping engine.

- *liferay-sample-commerce-tax-engine*

	Use Spring Boot to provide a new commerce tax engine.

- *liferay-sample-custom-element-1*

	A custom element can be self contained (i.e. does not depend on any external packages).

- *liferay-sample-custom-element-2*

	Build a custom element with `react-scripts`.

- *liferay-sample-custom-element-3*

	Build a custom element with `@angular/cli`.

- *liferay-sample-custom-element-4*

	Build a custom element that uses `react` and `react-dom` packages that Liferay makes publicly available through import maps.

- *liferay-sample-custom-element-5*

	Build a custom element that uses `@clayui/badge`, `react`, and `react-dom` packages that Liferay makes publicly available through import maps.

- *liferay-sample-custom-element-6*

	Build a custom element that uses `react`, `react-dom`, and `vite`.

- *liferay-sample-custom-element-7*

	Build a custom element that drives the search of a given frontend data set.

- *liferay-sample-custom-element-8*

	Build a custom element that takes the filtering of a given frontend data set over and offers a filter UI of its own.

- *liferay-sample-editor-config-contributor-1*

	Build a JavaScript function to configure CKEditor 4 and AlloyEditor editors.

- *liferay-sample-editor-config-contributor-2*

	Build a JavaScript function to add the CKEditor 5 WordCount plugin.

- *liferay-sample-editor-config-contributor-3*

	Add a custom timestamp button to CKEditor 5 as an example of a customer style plugin implementation without portal dependencies.

- *liferay-sample-editor-config-contributor-4*

	Add a custom style (Featured Content) to the CKEditor 5 Styles dropdown as an example of extending the editor's style definitions via a client extension.

- *liferay-sample-editor-config-contributor-5*

	Add a document link selector button to the CKEditor 5 link dialog as an example of augmenting the link plugin for document browsing without portal dependencies.

- *liferay-sample-editor-config-contributor-6*

	Enable the CKEditor 5 email editing plugins (Email Configuration Helper, Merge Fields, Template, and Export with Inline Styles, plus the nonpremium Layout Tables and Empty Block) as an example of registering premium and nonpremium plugins via a client extension.

- *liferay-sample-etc-cron*

	Use Spring Boot and OAuth (server to server) to read from and write to Liferay in timed intervals.

	To see this in action on your local machine:

	1. Go to `liferay-sample-workspace` and type `./gradlew startDockerContainer logsDockerContainer` to start Liferay.

	1. Go to `liferay-sample-workspace` and type `./gradlew :client-extensions:liferay-sample-etc-cron:deployDev`.

	1. Login to Liferay and go to Control Panel > Security > OAuth 2 Administration. Select `Liferay Sample Etc Cron`.

	1. Copy the client secret. Open `liferay-sample-workspace/client-extensions/liferay-sample-etc-cron/src/main/resources/application-default.properties` and replace `liferay-sample-etc-cron.oauth2.headless.server.client.secret=myfancypassword` with the real client secret.

	1. Go to `liferay-sample-workspace/client-extensions/liferay-sample-etc-cron` and type `./gradlew bootRun` to start Spring Boot.

- *liferay-sample-etc-frontend*

	Share code via JavaScript import maps to a custom element.

- *liferay-sample-etc-node*

	TODO

- *liferay-sample-etc-spring-boot*

	Use Spring Boot and OAuth (human to server) to interact with Liferay.

- *liferay-sample-fds-cell-renderer*

	Build a custom cell display in a frontend data set configured for table mode.

- *liferay-sample-fds-filter*

	Build a custom filter in a frontend data set.

- *liferay-sample-global-css-1*

	Add a global CSS to a company.

- *liferay-sample-global-css-2*

	Add a global CSS to a page.

- *liferay-sample-global-js-1*

	Add a global script element to a page.

- *liferay-sample-global-js-2*

	Add a global script element with attributes to a page.

- *liferay-sample-global-js-3*

	Add a global script element to a company.

- *liferay-sample-iframe-1*

	Add an IFrame widget with an interactive counter app.

- *liferay-sample-iframe-2*

	Add three IFrame widgets that render different Wikipedia topics.

- *liferay-sample-instance-settings*

	Customize instance settings with YAML by overridding OSGi configurations.

- *liferay-sample-js-import-maps-entry*

	Share jQuery via JavaScript import maps.

- *liferay-sample-site-initializer*

	TODO

- *liferay-sample-static-content*

	Deploy static content that is only accessible if you know the URL.

- *liferay-sample-theme-css-1*

	Extend the CSS of the `styled` theme.

- *liferay-sample-theme-css-2*

	Extend the CSS of the `styled` theme and apply it to admin pages.

- *liferay-sample-theme-css-3*

	Extend the CSS of the `unstyled` theme.

- *liferay-sample-theme-css-4*

	Extend the CSS of the `unstyled` theme and provide frontend token definitions.

- *liferay-sample-theme-favicon*

	Replace a theme's favicon.

- *liferay-sample-theme-spritemap-1*

	Replace a theme's spritemap with a single SVG file.

- *liferay-sample-theme-spritemap-2*

	Replace a theme's spritemap with a single SVG file that is built from multiple smaller SVG files.

## Functional Tests

The Playwright tests in `playwright` check that each sample client extension works against the Liferay version declared by `liferay.workspace.product` in `gradle.properties`. To check the samples against another version, change that property and run the tests again.

To stand up the environment and run every test, go to `liferay-sample-workspace` and type `./gradlew testFunctional`.

The first run builds the Liferay Docker image, starts PostgreSQL, Liferay, and the `liferay-sample-etc-node` and `liferay-sample-etc-spring-boot` microservices, and deploys every client extension. Later runs reuse the running environment and redeploy only the client extensions that changed. When `liferay.workspace.product` changes, the environment is recreated with an empty database.

The environment runs as its own Docker Compose project, so it does not touch the containers started by `scripts/bootstrap.sh`. Liferay is published at http://localhost:18080. Set `LIFERAY_FUNCTIONAL_TEST_PORT` to use another port.

- To pass arguments to Playwright, type `./gradlew testFunctional -Pplaywright.args="--grep 'Custom Element'"`.

- To rebuild the Liferay Docker image, for example after changing `configs/local`, type `./gradlew testFunctional -PrebuildDockerImage`.

- To remove the environment and its data, type `./gradlew stopTestEnvironment`.

To run the microservices next to the development environment, set `LIFERAY_COMPOSE_OVERRIDES=microservices` before running `scripts/bootstrap.sh`.

### Shared and Workspace-Owned Files

`workspaces/refresh_other_workspaces.sh` copies `playwright` to every other workspace, so the Gradle tasks, the Playwright configuration, and the shared `fixtures` and `helpers` are the same everywhere. Change them here, in `liferay-sample-workspace`.

Everything under `playwright/workspace` belongs to the workspace it lives in, and the sync never touches it:

- `workspace/tests` holds the workspace's specs.

- `workspace/fixtures/test.ts` extends the shared test with the workspace's own fixtures. Specs import `test` and `expect` from it.

- `workspace/helpers` holds the workspace's own helpers and page objects.

The Docker Compose override files follow the same split. `docker-compose.<name>.yaml` is shared and syncs; `docker-compose-<name>.yaml` belongs to the workspace. The functional test scripts include both `docker-compose.functional-test.yaml` and, when it exists, `docker-compose-functional-test.yaml`, so a workspace adds a service its tests need, such as an SMTP server, in the second file. The `microservices` override is included when the workspace has one, and every service it adds on top of `docker-compose.yaml` is started as a microservice.

### Configuration

`playwright/helpers/environment.ts` reads `playwright/.env`, then `playwright/.env.local`, then the environment, and later sources win. `.env.local` is ignored by Git. The variables are:

- `PORTAL_URL`: the Liferay to test against. Defaults to `http://localhost:${LIFERAY_FUNCTIONAL_TEST_PORT}`, with the port defaulting to 18080.

- `LIFERAY_ADMIN_EMAIL_ADDRESS` and `LIFERAY_ADMIN_PASSWORD`: the user the browser signs in as. Default to `test@liferay.com` and `test`.

- `LIFERAY_API_EMAIL_ADDRESS` and `LIFERAY_API_PASSWORD`: the administrator the API client authenticates as. `playwright/global-setup.ts` creates this user before the first test. It is separate from the browser's user because a basic authentication request as the browser's user signs that browser session out on some Liferay versions.