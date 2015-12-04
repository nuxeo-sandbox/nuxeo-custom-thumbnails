# nuxeo-custom-thumbnails


This plug-in displays specific thumbnails for `File` documents, depending on their mime-type or extension. This plug-in handles: `js`, `css`, `xml`, `html`, `java` and `as` (see below about HTML and XML).

!["Custom-Thumbnails"](https://raw.github.com/nuxeo-sandbox/nuxeo-custom-thumbnails/master/nuxeo-custom-thumbnails-plugin/src/main/resources/img/custom-thumbnails.jpg)

This is done by contributing a custom `ThumbnailFactory`, as explained [here](http://doc.nuxeo.com/x/sIPZ). This factory is registerded for every `File` document. When nuxeo requires a thumbnail for such document type, the plug-in checks the real type of the file (mime-type and if it's plain text, it looks at the extension), and either changes the thumbnail or uses the default factory.

Thumbnails are stored in the `img` folder of  `resources`, and the plug-in does not _store_ the image in each document, it dynamically returns the image when nuxeo needs it instead.

If you extend the `File` document and still want to use this plug-in, you can register the following contribution, which uses the plug-in's factory class (replace MyDocType with your document type):

```
<extension
  target="org.nuxeo.ecm.core.api.thumbnail.ThumbnailService"
  point="thumbnailFactory">
  
  <thumbnailFactory
    name="thumbnailsForMyDocType"
    docType="MyDocType"
    factoryClass="org.nuxeo.custom.thumbnails.ThumbnailFileFactory" />
    
</extension>
```

_Note_: The `thumbnailFactory` extension point also allows using a `Facet`

### Build
Assuming `maven` is installed on you computer, you can just download these sources, then `mvn clean install`. The Marketplace Package will then be ready to install:

```
# Clone this repo
cd /path/to/to/a/folder
git clone https://github.com/nuxeo-sandbox/nuxeo-custom-thumbnails.git
# Build with maven
cd nuxeo-custom-thumbnails
mvn clean install
# The MP is in nuxeo-custom-thumbnails/nuxeo-custom-thumbnails-mp_target
```

### About HTML/XML, and `Note` vs `File`
By default, nuxeo creates `Note` documents for HTML and XML (depending on the version). This can be disabled by changing the `NoteImporter` in an XML contribution:

```
<extension
  target="org.nuxeo.ecm.platform.filemanager.service.FileManagerService"
  point="plugins">
  
  <plugin name="NoteImporter" enabled="false" />
  
</extension>
    
```

## License
(C) Copyright 2014 Nuxeo SA (http://nuxeo.com/) and others.

All rights reserved. This program and the accompanying materials
are made available under the terms of the GNU Lesser General Public License
(LGPL) version 2.1 which accompanies this distribution, and is available at
http://www.gnu.org/licenses/lgpl-2.1.html

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
Lesser General Public License for more details.

Contributors:
Thibaud Arguillere (https://github.com/ThibArg)

## About Nuxeo

Nuxeo provides a modular, extensible Java-based [open source software platform for enterprise content management](http://www.nuxeo.com) and packaged applications for Document Management, Digital Asset Management and Case Management. Designed by developers for developers, the Nuxeo platform offers a modern architecture, a powerful plug-in model and extensive packaging capabilities for building content applications.
