/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

import test from 'ava';

import {Token, TokenType, getToken} from '../lib/tokens';

interface TestCase {
	expectedMatchText: string[];
	expectedTokenType: TokenType;
	input: string;
	optionalDescription?: string;
}

const cursor = '|';

const testCases: TestCase[] = [
	{
		expectedMatchText: ['TestCaseName'],
		expectedTokenType: 'testCaseName',
		input: `test |TestCaseName {`,
	},
	{
		expectedMatchText: ['Test_Case-Name3Foo'],
		expectedTokenType: 'testCaseName',
		input: `test |Test_Case-Name3Foo {`,
		optionalDescription: 'with numbers and special characters',
	},
	{
		expectedMatchText: ['testCaseName'],
		expectedTokenType: 'testCaseName',
		input: `test |testCaseName {`,
		optionalDescription: 'with starting lower case',
	},
	{
		expectedMatchText: ['variableName'],
		expectedTokenType: 'variable',
		input: 'foo bar ${|variableName} baz',
	},
	{
		expectedMatchText: ['variableName'],
		expectedTokenType: 'variable',
		input: 'foo bar ${variableName|} baz',
		optionalDescription: 'cursor at the end',
	},
	{
		expectedMatchText: ['TextInput'],
		expectedTokenType: 'pathFileName',
		input: 'locator1 = "|TextInput#MAIL_DOMAIN",',
	},
	{
		expectedMatchText: ['TextInput'],
		expectedTokenType: 'pathFileName',
		input: 'locator1 = "TextInput|#MAIL_DOMAIN",',
		optionalDescription: 'cursor at the end',
	},
	{
		expectedMatchText: ['TextInput', 'MAIL_DOMAIN'],
		expectedTokenType: 'pathLocator',
		input: 'locator1 = "TextInput#|MAIL_DOMAIN",',
	},
	{
		expectedMatchText: ['TextInput', 'MAIL_DOMAIN2_FOO'],
		expectedTokenType: 'pathLocator',
		input: 'locator1 = "TextInput#|MAIL_DOMAIN2_FOO",',
		optionalDescription: 'with number',
	},
	{
		expectedMatchText: ['JSONCurlUtil'],
		expectedTokenType: 'utilClass',
		input: 'var response = |JSONCurlUtil.post(${curl});',
	},
	{
		expectedMatchText: ['JSONCurlUtil'],
		expectedTokenType: 'utilClass',
		input: 'var response = JSONCurlUtil|.post(${curl});',
		optionalDescription: 'cursor at the end',
	},
	{
		expectedMatchText: ['JSONCurlUtil', 'post'],
		expectedTokenType: 'utilClassMethod',
		input: 'var response = JSONCurlUtil.|post(${curl});',
	},
	{
		expectedMatchText: ['SomeClass'],
		expectedTokenType: 'className',
		input: 'var response = |SomeClass.someMethod(${curl});',
	},
	{
		expectedMatchText: ['SomeClass'],
		expectedTokenType: 'className',
		input: 'var response = SomeClass|.someMethod(${curl});',
		optionalDescription: 'cursor at the end',
	},
	{
		expectedMatchText: ['SomeClass', 'someMethod'],
		expectedTokenType: 'methodInvocation',
		input: 'var response = SomeClass.|someMethod(${curl});',
	},
	{
		expectedMatchText: ['SomeClass', 'foo-Bar_Baz8'],
		expectedTokenType: 'methodInvocation',
		input: 'var response = SomeClass.|foo-Bar_Baz8(${curl});',
		optionalDescription: 'with numbers and special characters',
	},
	{
		expectedMatchText: ['fooBar'],
		expectedTokenType: 'methodDefinition',
		input: 'macro |fooBar {',
		optionalDescription: 'macro',
	},
	{
		expectedMatchText: ['foo-Bar_Baz8'],
		expectedTokenType: 'methodDefinition',
		input: 'macro |foo-Bar_Baz8 {',
		optionalDescription: 'macro with numbers and special characters',
	},
	{
		expectedMatchText: ['fooBar'],
		expectedTokenType: 'methodDefinition',
		input: 'function |fooBar {',
		optionalDescription: 'function',
	},
	{
		expectedMatchText: ['foo-Bar_Baz8'],
		expectedTokenType: 'methodDefinition',
		input: 'function |foo-Bar_Baz8 {',
		optionalDescription: 'function with numbers and special characters',
	},
];

const macro = test.macro({
	exec(t, testCase: TestCase) {
		const cursorIndex = testCase.input.indexOf(cursor);
		const inputText = testCase.input.replace(cursor, '');

		const result = getToken(inputText, cursorIndex) as Token;

		t.assert(
			result,
			`No token found for text "${inputText}" at cursor position ${cursorIndex}`
		);
		t.is(result.type, testCase.expectedTokenType, 'Token type');

		const actualMatches = result.match.captures.slice(1);

		t.is(
			actualMatches.length,
			testCase.expectedMatchText.length,
			`Expected ${testCase.expectedMatchText} but got ${actualMatches}`
		);

		for (
			let index = 0;
			index < testCase.expectedMatchText.length;
			index++
		) {
			t.is(
				actualMatches[index],
				testCase.expectedMatchText[index],
				'Match text'
			);
		}
	},
	title(providedTitle = '', testCase: TestCase) {
		let title = `${providedTitle} > ${testCase.expectedTokenType}`;

		if (testCase.optionalDescription) {
			title = `${title} > ${testCase.optionalDescription}`;
		}

		return title;
	},
});

for (const testCase of testCases) {
	test('getToken', macro, testCase);
}
