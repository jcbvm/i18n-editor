# i18n-editor [![Build Status](https://travis-ci.org/jcbvm/ember-i18n-editor.svg?branch=master)](https://travis-ci.org/jcbvm/ember-i18n-editor)

This application lets you manage multiple translations files at once. The application can handle both `JSON` and `ES6` formatted files (the latter will save the JSON wrapped in an ES6 module).

This project is mainly designed for [ember-i18n](https://github.com/jamesarosen/ember-i18n) (>= v4.0.0) uses, but can also be used without it.

<img src="https://raw.github.com/jcbvm/ember-i18n-editor/master/screenshot.jpg?2" width="600">

## Features

- Editing multiple translation files at once.
- Creating new translations/locales or editing existing ones.
- Renaming, duplicating, creating or deleting individual translations.
- Drag&Drop directories for editing.
- Minifying output.

## Requirements

The application requires java 8 to be installed on your system.<br>
http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html

## Download

You can download the latest release by going to [this page](https://github.com/jcbvm/ember-i18n-editor/releases/latest) and downloading the `.zip` file.<br> If you're on Windows you can install the application by running the `.exe` file. If you're on Linux or Mac you can run the application by executing the `.jar` file.<br>

## Usage

When you open the editor for the first time, it will ask you to choose a folder, this can be any folder you want to put your translation files in (normally when using ember-i18n this will be your `app/locales/` directory). 

To add a locale, go to `Edit > Add Locale` where you can choose whether you want to create a `JSON` or an `ES6` file, enter the locale you want to create the tranlations for and press OK. A new directory will be created for you named after the locale you entered, with a `translations.json` file if you chose for JSON format and a `translations.js` file if you chose for ES6 format. This is where your translations will be put in.

From here you can begin adding translations (either via the top menu, via the right click menu in the left side panel or via the key field at the bottom of the left side panel).

## License

This project is released under the MIT license.
