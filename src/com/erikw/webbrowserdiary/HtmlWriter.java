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
    // this class will take in a collection of URL info's and creates
    // a snippet.
    // A snippet starts by greeting user and has a bulleted list of urls
    
    // each URL will have a heading which was set by the user when they edit
    // the tree.
    
    public static void writeToHTML(String htmlFileName,
            Collection<UrlInfo> summaries)
    {
        BufferedWriter htmlWriter = null;
        try {
            File htmlFile = new File(htmlFileName);
            if(htmlFile.exists())
            {
                htmlFile.delete();
            }
            htmlFile.createNewFile();
            htmlWriter = new BufferedWriter(new FileWriter(htmlFile));
            htmlWriter.write("<html><body>Hi,<br/><br/>" + 
                    "Here are some interesting snippets <br/><ul/>");
            
            for(UrlInfo urlInfo:summaries)
            {
                String body = "<li><b><a href=\""+urlInfo.getUrl()+
                        "\"target=\"blank\" a>" +
                        urlInfo.getHeadline()+"</a></b>:"+
                        urlInfo.getSummary()+"</li>";
                htmlWriter.write("<br/>" +body);
            }
            htmlWriter.write("</ul></br></body></html>");
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        
        finally{
            
            try
            {
                htmlWriter.close();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }
  
    
}
