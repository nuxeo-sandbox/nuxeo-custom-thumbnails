/*
 * (C) Copyright 2015 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     thibaud
 */
package org.nuxeo.custom.thumbnails;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.ecm.core.api.thumbnail.ThumbnailFactory;
import org.nuxeo.ecm.platform.thumbnail.factories.ThumbnailDocumentFactory;

import com.google.common.io.Files;

/**
 * 
 *
 * @since 7.2
 */
public class ThumbnailFileFactory implements ThumbnailFactory {

    private static Log log = LogFactory.getLog(ThumbnailFileFactory.class);

    protected static String[] HANDLED_EXTENSIONS = { "as", "css", "html",
            "java", "js", "xml" };

    protected static HashMap<String, FileBlob> thumbnails = null;

    private static String LOCK = "ThumbnailFileFactory-LOCK";

    @Override
    public Blob getThumbnail(DocumentModel inDoc, CoreSession inSession)
            throws ClientException {

        Blob result = null;
        if (inDoc.hasSchema("file")) {
            Blob b = (Blob) inDoc.getPropertyValue("file:content");
            if (b != null) {
                String ext = getExtensionForBlob(b);
                result = getThumbnail(ext);
            }
        }

        if (result == null) {
            ThumbnailFactory defaultDocFactory = new ThumbnailDocumentFactory();
            result = defaultDocFactory.computeThumbnail(inDoc, inSession);
        }

        return result;
    }

    @Override
    public Blob computeThumbnail(DocumentModel inDoc, CoreSession inSession) {

        Blob result = null;
        boolean needDefault = true;

        if (inDoc.hasSchema("file")) {

            Blob b = (Blob) inDoc.getPropertyValue("file:content");
            if (b == null) {
                needDefault = false;
            } else {
                String ext = getExtensionForBlob(b);
                result = getThumbnail(ext);

                // We don't want to store the blob in the document, no need for
                // that since the image is always the same, so we return it in
                // getThumbnail(). Still: we need to make sure we have one
                // and if not, let the default factory handle it.
                //
                // So, if we know we have one, we return null and make sure the
                // default factory is not involved
                if (result != null) {
                    result = null;
                    needDefault = false;
                }
            }

        } else {
            needDefault = false;
        }

        if (result == null && needDefault) {
            ThumbnailFactory defaultDocFactory = new ThumbnailDocumentFactory();
            result = defaultDocFactory.computeThumbnail(inDoc, inSession);
        }

        return result;
    }

    protected Blob getThumbnail(String inExtension) {

        if (thumbnails == null) {
            synchronized (LOCK) {
                if (thumbnails == null) {
                    thumbnails = new HashMap<String, FileBlob>();

                    String fileRelativePath;
                    FileBlob fb;
                    InputStream in = null;
                    for (String ext : HANDLED_EXTENSIONS) {
                        
                        fileRelativePath = "/img/" + ext + "-100.png";
                        in = getClass().getResourceAsStream(fileRelativePath);
                        try {
                            fb = new FileBlob(in);
                            thumbnails.put(ext, fb);
                        } catch (Exception e) {
                            log.error("Failed to get image in '"
                                    + fileRelativePath + "'", e);
                        } finally {
                            /*
                            if(in != null) {
                                try {
                                    in.close();
                                } catch (IOException e) {
                                    // Ignore
                                }
                            }
                            */
                        }
                        
                    }
                }
            }
        }

        return thumbnails.get(inExtension.toLowerCase());
    }

    /*
     * This one could probably be tuned and enhanced.
     */
    protected String getExtensionForBlob(Blob inBlob) {

        String ext = "";
        if (inBlob == null) {
            return null;
        }
        String mimeType = inBlob.getMimeType();
        if (StringUtils.isNotBlank(mimeType)) {
            switch (mimeType) {
            case "text/html":
                ext = "html";
                break;
            case "text/css":
                ext = "css";
                break;
            case "text/javascript":
                ext = "js";
                break;
            case "text/xml":
                ext = "xml";
                break;

            default:
                String fileName = inBlob.getFilename();
                int pos = fileName.lastIndexOf('.');
                if (pos > -1) {
                    String fileExt = fileName.substring(pos + 1);
                    ext = fileExt.toLowerCase();
                    switch (ext) {
                    case "html":
                    case "htm":
                        ext = "html";
                        break;
                    }
                }
                break;
            }
        }

        return ext;

    }

}
