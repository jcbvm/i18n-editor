# Ember i18n Editor [![Build Status](https://travis-ci.org/jcbvm/ember-i18n-editor.svg?branch=master)](https://travis-ci.org/jcbvm/require-i18next)

This application lets you manage multiple translations files for [Ember-I18n](https://github.com/jamesarosen/ember-i18n), but can also be used without it. The application saves the translations files in `JSON` or `ES6` format (the latter will save the JSON wrapped in an ES6 module).

<img src="https://raw.github.com/jcbvm/ember-i18n-editor/master/screenshot.jpg?1" width="600">

## NOTE

When you want to use this application for Ember-I18n, version 4.0 or greater is required.

## Installation

This package comes with a `.exe` file and a `.jar` file.<br>
The application requires java 8 to be intalled on your system.

<i>Currently the `.exe` file is not signed yet, so it will show you a warning message about unknown publiser.</i>

## Usage

When you open the program for the first time, it will ask you to choose a folder, this can be any folder you want to put your translation files in (normally when using Ember-I18n this will be your `app/locales/` directory). 

To add a locale, go to `Edit > Add Locale` where you can choose whether you want to create a `JSON` or an `ES6` file, enter the locale you want to create the tranlations for and press OK. A new directory will be created for you named after the locale you entered, with a `translations.json` file if you chose for JSON format and a `translations.js` file if you chose for ES6 format. This is where your translations will be put in.

From here you can begin adding translations (either via the top menu, via the right click menu in the left side panel or via the key field at the bottom of the left side panel).

## License

This project is released under the MIT license.
