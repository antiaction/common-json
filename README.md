[![Build Status](https://travis-ci.org/nclarkekb/antiaction-common-json.png?branch=master)](https://travis-ci.org/nclarkekb/antiaction-common-json)

antiaction-common-json
======================

Java JSON library

## History ##

### version 0.4.0 ###

Work on improving JSON object mapper for array fields.

* Support for array fields in object mapper.
* Improved JSON decoder exceptions including (x, y) in source stream.
* Name annotation to map object field to/from differently named JSON kay.
* NullValues annotation to allow null values in arrays.
* Converter annotation and support for callbacks in object mapper.
* Work in progress for top level array and native collection types object mapping.

### version 0.3.0 ###

Work on the Object to JSON structure mapper.

* Recursive Object to JSON structure mapping.
* Pretty encoding option. (Linefeeds, indentation and spaces)

### version 0.2.0 ###

Work on the JSON structure to Object mapper.
Comprehensive unit testing of the Object mapper.

* Added some annotations for nullable, ignore and json.
* Added support for byte[] in the JSON String implementation.
* Added javadoc to most of the classes.
* Recursive JSON structure to Object mapping.

### version 0.1.0 ###

Main focus on JSON text decoding/encoding.
Comprehensive unit testing of core functionality.

Java types supported:
* Null
* Boolean
* Integer
* Long
* Float
* Double
* BigInteger
* BigDecimal
* String

