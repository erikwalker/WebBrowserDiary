/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erikw.webbrowserdiary;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

/**
 *
 * @author Erik.Walker
 */

public class HtmlWriter {

    // recall that HTML = HyperText Markup Language is a way to display text and images in browsers
    // HTML files have a hierarchy of nested tags, for instance <html> and </html> act as the outermost
    // tags.

    public static void writeToHTML(String htmlFileName, Collection<UrlInfo> summaries) {
        BufferedWriter htmlWriter = null;
        try{
            File htmlFile = new File(htmlFileName);
            if (htmlFile.exists()) {
                // we overwrite this file without prompting, a bit rude, but OK for this example!
                htmlFile.delete();
            }
            htmlFile.createNewFile();
            htmlWriter = new BufferedWriter(new FileWriter(htmlFile));

            htmlWriter.write("<html><body>Hi, <br/> Here are your notes so far <br/><ul/>");
            for (UrlInfo urlInfo : summaries) {
                String body = "<li><b><a href=\"" + urlInfo.getUrl() + "\" target=\"blank\" a>"
                        + urlInfo.getHeadline() + "</a></b><br/>"
                        + urlInfo.getSummary() + "</li>";
                htmlWriter.write("<br/>" + body);
            }

            // what did we do in the loop above: create the html for each URL with a nice link
            // and with some formatting (bullet points, bold for the headline). Note also how the
            // target = "blank" is specified to make sure that the links open in a new window. Also
            // note how the double quotes are escaped (embedded inside a string which itself is
            // delimited by double quotes) using the preceding backslash

            htmlWriter.write("</ul><br/></body></html>");


        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                htmlWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
