#!/usr/bin/env zx

import 'zx/globals'
import assert from 'node:assert/strict'

$.verbose = (Boolean(argv.verbose) || Boolean(argv.v)) ?? false;

const json = Boolean(argv.json) ?? false;
const schema = Boolean(argv.schema) ?? false;

const fileLines = (filePath) => fs.readFileSync(filePath, {encoding: 'utf8'}).trim().split('\n')
const processLines = (processOutput) => processOutput.stdout.trim().split('\n')

const schemaDataTypes = {
    'boolean': 'boolean',
    'LocalizedValuesMap': 'object',
    'int': 'number',
    'long': 'number',
    'String': 'string',
    'String[]': 'array',
    'float': 'number'
}

const topLevel = (await $`git rev-parse --show-toplevel`).stdout.trim()
const languagePropertiesFilePath = 'modules/apps/portal-language/portal-language-lang/src/main/resources/content/Language.properties'

const langKeys = {};
for (const line of fileLines(`${topLevel}/${languagePropertiesFilePath}`)) {
    const groups = /^(?<key>.*)=(?<value>.*)$/.exec(line)?.groups
    if (!groups) continue;

    const {key, value} = groups

    langKeys[key] = value
}

const mergeRegexValues = (targetObject, string, regex, fn) => {
    const groups = regex.exec(string)?.groups
    if (!groups) return

    for (const [key, value] of Object.entries(groups)) {
        targetObject[key] = value
    }

    if (fn) {
        fn(groups)
    }
}

const objectDefs = [];

const gitGrepFiles = await $`git grep -l 'scope = ExtendedObjectClassDefinition.Scope.(COMPANY|GROUP)' -- '*Configuration.java'`

outer:
for (const filePath of processLines(gitGrepFiles)) {
    const lines = fs.readFileSync(filePath, {encoding: 'utf8'}).trim().split('\n');

    let objectDef = {attributeDefs: []}

    const defaultAttributeDef = {required: true};

    let attributeDef = {...defaultAttributeDef}
    for (const line of lines) {
        if (!objectDef.interfaceName) {
            mergeRegexValues(objectDef, line, /\bid = "(?<pid>com\..+)"/)
            mergeRegexValues(objectDef, line, /\bcategory = "(?<category>[^"]*)"/)
            mergeRegexValues(objectDef, line, /\bdescription = "(?<description>[^"]*)"/, ({description}) => {
                objectDef.description = langKeys[description]
            })
            mergeRegexValues(objectDef, line, /\bname = "(?<title>[^"]*)"/, ({title}) => {
                objectDef.title = langKeys[title]
            })

            // Commit objectDef
            mergeRegexValues(objectDef, line, /public @?interface (?<interfaceName>[A-Z][A-Za-z\d]+)\b/)

            continue;
        }

        if (objectDef.pid === undefined) {
            continue outer;
        }

        mergeRegexValues(attributeDef, line, /\bdeflt = "(?<defaultValue>[^"]*)"/, () => {
            if (attributeDef.defaultValue.startsWith("${")) {
                delete attributeDef.defaultValue
            }
        })
        mergeRegexValues(attributeDef, line, /\bdescription = "(?<description>[^"]*)"/, ({description}) => {
            attributeDef.description = langKeys[description]
        })
        mergeRegexValues(attributeDef, line, /\bmax = "(?<max>[^"]*)"/)
        mergeRegexValues(attributeDef, line, /\bmin = "(?<min>[^"]*)"/)
        mergeRegexValues(attributeDef, line, /\bname = "(?<title>[^"]*)"/, ({title}) => {
            attributeDef.title = langKeys[title]
        })
        mergeRegexValues(attributeDef, line, /\boptionLabels = \{(?<optionLabels>[^\{\}]*)\}/, ({optionLabels}) => {
            attributeDef.optionLabels = []
            for (const match of optionLabels.matchAll(/"(?<optionLabel>[^"]+)"/g)) {
                attributeDef.optionLabels.push(match.groups.optionLabel)
            }
        })
        mergeRegexValues(attributeDef, line, /\boptionValues = \{(?<optionValues>[^\{\}]*)\}/, ({optionValues}) => {
            attributeDef.optionValues = []
            for (const match of optionValues.matchAll(/"(?<optionValue>[^"]+)"/g)) {
                attributeDef.optionValues.push(match.groups.optionValue)
            }
        })
        mergeRegexValues(attributeDef, line, /\b(?<requiredInput>requiredInput = )/)
        mergeRegexValues(attributeDef, line, /\brequired = (?<required>true|false)/, ({required}) => attributeDef.required = required === "true")
        mergeRegexValues(attributeDef, line, /\b(?<deprecated>@Deprecated)/, () => attributeDef.deprecated = true)

        // Commit attributeDef
        mergeRegexValues(attributeDef, line, /\s+public(default)? (?<type>\w+|\S+) (?<name>\w+)\(\)/, ({type}) => {
            attributeDef.type = schemaDataTypes[type]

            // if (attributeDef.defaultValue || attributeDef.defaultValue === "") {
            //     if (attributeDef.type === 'number') {
            //         attributeDef.defaultValue = attributeDef.defaultValue.length ? Number(attributeDef.defaultValue) : 0
            //         console.log(`${objectDef.pid}#${attributeDef.name} MAX is ${attributeDef.defaultValue} and type is ${typeof attributeDef.defaultValue}`)
            //     }
            //     if (attributeDef.type === 'boolean') {
            //         attributeDef.defaultValue = attributeDef.defaultValue === "true"
            //     }
            // }

            if (attributeDef.requiredInput) attributeDef.required = true

            objectDef.attributeDefs.push(attributeDef)

            attributeDef = {...defaultAttributeDef}
        })
    }
    objectDefs.push(objectDef)
}

if (json) {
    console.log(JSON.stringify(objectDefs))
    process.exit()
} else if (schema) {
    const schema = {oneOf: [], properties: {pid: {enum: []}}}

    for (const objectDef of objectDefs) {
        const {attributeDefs, category, description: oDescription, interfaceName, pid, title: oTitle} = objectDef
        assert(pid !== undefined, `no pid for ${interfaceName}`)
        assert(attributeDefs.length > 0, `no attributes for ${pid}`)

        const typeSchema = {
            properties: {
                pid: {
                    const: pid
                }
            },
            required: ['pid']
        }
        if (oTitle) typeSchema.title = oTitle
        if (oDescription) typeSchema.description = oDescription
        if (oTitle) typeSchema.properties.pid.title = oTitle
        if (oDescription) typeSchema.properties.pid.description = oDescription

        for (const {type, defaultValue = '', deprecated = false, description = '', max, min, name, optionValues = [], required = false, title} of attributeDefs) {
            assert(type !== undefined, `no datatype for ${pid}:${name}`)
            assert(name !== undefined, `no name for ${pid}:${name}`)

            const propertySchema = {type};

            if (type === 'array') {
                propertySchema.items = {type: "string"};
            } else if (type === 'object') {
                propertySchema.properties = {}
            }

            if (defaultValue) {
                if (type === 'number') propertySchema.default = Number(defaultValue)
                else if (type === 'boolean') propertySchema.default = defaultValue === "true"
                else propertySchema.default = defaultValue
            }
            if (deprecated) propertySchema.deprecated = true
            if (description) propertySchema.description = description
            if (max) {
                if (type === 'number') propertySchema.max = Number(max)
                if (type === 'string') propertySchema.maxLength = max
            }
            if (min) {
                if (type === 'number') propertySchema.min = Number(min)
                if (type === 'string') propertySchema.minLength = min
            }
            if (optionValues.length > 0) {
                if (type === 'array') propertySchema.items.enum = optionValues
                else propertySchema.enum = optionValues
            }
            if (title) propertySchema.title = title

            typeSchema.properties[name] = propertySchema
            if (required) typeSchema.required.push(name)
        }

        schema.oneOf.push(typeSchema)
        schema.properties.pid.enum.push(pid)
    }

    console.log(JSON.stringify(schema))
    process.exit()
}
