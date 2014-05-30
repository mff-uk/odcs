/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.xrg.intlib.extractor.simplexslt;

import java.io.*;

import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.s9api.*;

public class FDALabelsTriplifier {

    private static String fdaOutputFolderName = "c:\\Users\\martin\\Documents\\WORK\\PROJECTS\\#NASE\\EHEALTH\\LD\\_OUTPUTS\\FDALABELS2LD\\output";

    public static void main(String[] args) {

        try {
            File fdaInputFolder = new File("c:\\Users\\martin\\Documents\\WORK\\PROJECTS\\#NASE\\EHEALTH\\DATASOURCES\\FDA_LABELS\\data\\2013-04-30-all\\prescription-xml");

            File stylesheet = new File("c:\\Users\\martin\\Documents\\WORK\\PROJECTS\\#NASE\\EHEALTH\\LD\\_OUTPUTS\\FDALABELS2LD\\xls\\fda2ld.xslt");

            Processor proc = new Processor(false);
            XsltCompiler compiler = proc.newXsltCompiler();
            XsltExecutable exp = compiler.compile(new StreamSource(stylesheet));

            int counter = 0;
            int group = 0;
            int groupmod = 1000;

            File[] fdaInputFiles = fdaInputFolder.listFiles();

            int totalCount = fdaInputFiles.length;
            int wrongCounter = 0;

            for (File fdaLabel : fdaInputFiles) {

                try {

                    XdmNode source = proc.newDocumentBuilder().build(new StreamSource(fdaLabel));

                    Serializer out = new Serializer();
                    out.setOutputProperty(Serializer.Property.METHOD, "text");
                    out.setOutputProperty(Serializer.Property.INDENT, "yes");
                    group = counter / groupmod;

                    File fdaLabelTtl = new File(fdaOutputFolderName + "\\out" + group + "\\" + fdaLabel.getName().substring(0, fdaLabel.getName().lastIndexOf(".")) + ".ttl");
                    out.setOutputFile(fdaLabelTtl);

                    XsltTransformer trans = exp.load();

                    trans.setInitialContextNode(source);
                    trans.setDestination(out);
                    trans.transform();

                    counter++;
                    System.out.println(counter + "/" + totalCount + "[" + wrongCounter + "]: " + group + "\\" + fdaLabelTtl.getName() + " created successfully.");

                } catch (SaxonApiException e) {
                    System.out.println("Unable to transform " + fdaLabel.getName());
                    reportAndCleanUnprocessedFile(fdaLabel, e.getClass().toString());
                    wrongCounter++;
                    //e.printStackTrace();
                } catch (StackOverflowError e) {
                    System.out.println("Unable to transform " + fdaLabel.getName());
                    reportAndCleanUnprocessedFile(fdaLabel, e.getClass().toString());
                    wrongCounter++;
                    System.out.println(e.getClass());
                    //e.printStackTrace();
                }

            }

            File fdaOutputFolder = new File(fdaOutputFolderName);
            OutputStream fdaOutputStream = new FileOutputStream(new File(fdaOutputFolderName + "\\output.ttl"));

            byte[] buf = new byte[1 << 18];
            InputStream fdaInputStream = new FileInputStream(new File("c:\\Users\\martin\\Documents\\WORK\\PROJECTS\\#NASE\\EHEALTH\\LD\\_OUTPUTS\\FDALABELS2LD\\input\\spl_output_prefixes.ttl"));
            int b = 0;
            while ((b = fdaInputStream.read(buf)) >= 0) {
                fdaOutputStream.write(buf, 0, b);
                fdaOutputStream.flush();
            }
            fdaInputStream.close();

            for (File fdaOutputSubFolder : fdaOutputFolder.listFiles()) {

                for (File inFdaName : fdaOutputSubFolder.listFiles()) {
                    fdaInputStream = new FileInputStream(inFdaName);
                    b = 0;
                    while ((b = fdaInputStream.read(buf)) >= 0) {
                        fdaOutputStream.write(buf, 0, b);
                        fdaOutputStream.flush();
                    }
                    fdaInputStream.close();

                    inFdaName.deleteOnExit();
                }

                fdaOutputSubFolder.deleteOnExit();

            }

            fdaOutputStream.close();

        } catch (SaxonApiException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void reportAndCleanUnprocessedFile(File file, String message) {
        try {
            if (file.exists()) {
                file.delete();
            }
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fdaOutputFolderName + "\\unprocessed.err", true)));
            out.println(file + ": " + message);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
