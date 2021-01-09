## Jan 8, 2020
### 2.12.8
* Minor change: Adding HST column bundle to display project proposal 
column so HAP products can be identified and set column display 
order to be unique to HST collection.

## Nov 3, 2020
### 2.12.7
* Minor change: Further constrain start date quick links to include collection and instrument name.

## Nov 2, 2020
### 2.12.6
* Fix for github issue: https://github.com/opencadc/caom2ui/issues/182 
* Additional results columns displayed for Upload Target file not going away when switching
to a single target search. (And the columns didn't show up under certain search combinations.)

## Oct 30, 2020
### 2.12.5
* Change: support 'Do Spatial Cutout' for Upload Target files

## Oct 2, 2020
### 2.12.4
* Minor change: Bug fix for upload target file radius being ignored during search.

## June 29th, 2020
### 2.12.3
* Minor change: Upload target, RA and Dec added to search results if Upload Targets
file is used.  

## June 9th, 2020
### 2.12.2
* Minor change: 'Do Spatial Cutout' checkbox disabled when a target file is selected in 
the Upload Targets section of the search form. 

## September 12th, 2019
### 2.11.0
* Major change: remove support for MAQ toggle
* Change connection pool creation and configuration

## April 12th, 2018
### 2.7.0
* Added option to the [cadc-votv VOTable viewer](https://github.com/opencadc/web/tree/master/cadc-votv) to disable auto setting the one-click download link.  This will allow for setting it later on, as is done in Advanced Search now by reading the data from the DataLink server.
* Added new logic when getting URLs for Previews.

## April 11th, 2018
### 2.6.9
* Fix for previews not showing up for some collections
* Encode the ID parameter of the request to /caom2ui

## April 2nd, 2018
### 2.6.8
* Various small bug fixes.

## March 8th, 2018
### 2.6.1
* Fix to the footprint viewer plugin to handle Circle footprints with an updated AladinLite plugin.

## March 5th, 2018

### 2.6.0
* Major change: Add support for a toggle switch to support querying alternate TAP services.
  * This is driven by the CADC's MAQ (Multi Archive Query) project to provide access to external data sources.
* Upgraded support for the browser tests to Selenium 3.

