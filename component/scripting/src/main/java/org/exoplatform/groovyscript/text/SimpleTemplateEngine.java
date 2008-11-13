package org.exoplatform.groovyscript.text ;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import groovy.lang.Writable;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import groovy.text.Template;
import groovy.text.TemplateEngine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.exoplatform.commons.utils.Text;

/**
 * This simple template engine uses JSP <% %> script and <%= %> expression syntax.  It also lets you use normal groovy expressions in
 * the template text much like the new JSP EL functionality.  The variable 'out' is bound to the writer that the template is being written to.
 * 
 * @author sam
 * @author Christian Stein
 */
public class SimpleTemplateEngine extends TemplateEngine {

  private final boolean verbose;

  public SimpleTemplateEngine() {
    this(false);
  }

  public SimpleTemplateEngine(boolean verbose) { this.verbose = verbose; }

  public Template createTemplate(Reader reader) throws CompilationFailedException, IOException {
    SimpleTemplate template = new SimpleTemplate();
    GroovyShell shell = new GroovyShell(Thread.currentThread().getContextClassLoader());
    String script = template.parse(reader);
    if (verbose) {
      System.out.println("\n-- script source --");
      System.out.print(script);
      System.out.println("\n-- script end --\n");
    }

    //
    CompilerConfiguration config = new CompilerConfiguration();
    config.setScriptBaseClass(ExoScript.class.getName());
    byte[] bytes = script.getBytes(config.getSourceEncoding());
    InputStream in = new ByteArrayInputStream(bytes);
    GroovyCodeSource gcs = new GroovyCodeSource(in, "fic", "/groovy/shell");
    GroovyClassLoader loader = new GroovyClassLoader(Thread.currentThread().getContextClassLoader(), config);
    loader.parseClass(gcs, false);
    template.scriptClass = loader.parseClass(script);
    return template;
  }

  public static abstract class ExoScript extends Script {

    private PrintWriter printer;

    protected ExoScript() {
    }

    protected ExoScript(Binding binding) {
      super(binding);
    }

    @Override
    public void println(Object o) {
      print(o);
      println();
    }

    @Override
    public void println() {
      printer.println();
    }

    @Override
    public void print(Object o) {
      if (o instanceof Text) {
        try {
          ((Text)o).writeTo(printer);
        }
        catch (IOException e) {
          e.printStackTrace();
        }
      } else {
        printer.print(o);
      }
    }
  }

  private static class SimpleTemplate implements Template {

    protected Class scriptClass;

    public Writable make() {
      return make(null);
    }

    public Writable make(final Map map) {
      return new Writable() {
        /**
         * Write the template document with the set binding applied to the writer.
         *
         * @see groovy.lang.Writable#writeTo(java.io.Writer)
         */
        public Writer writeTo(Writer writer) {
          Binding context;
          if (map == null)
            context = new Binding();
          else
            context = new Binding(map);

          // Normally we should have a PrintWriter to avoid the cost creation of one
          PrintWriter printer;
          if (writer instanceof PrintWriter) {
            printer = (PrintWriter)writer;
          } else {
            printer = new PrintWriter(writer);
          }

          //
          ExoScript script = (ExoScript)InvokerHelper.createScript(scriptClass, context);
          script.printer = printer;
          script.setProperty("out", script.printer);
          script.run();
          script.printer.flush();
          return writer;
        }

        /**
         * Convert the template and binding into a result String.
         *
         * @see java.lang.Object#toString()
         */
        public String toString() {
          try {
            StringWriter sw = new StringWriter();
            writeTo(sw);
            return sw.toString();
          } catch (Exception e) {
            return e.toString();
          }
        }
      };
    }

    /**
     * Parse the text document looking for <% or <%= and then call out to the appropriate handler, otherwise copy the text directly
     * into the script while escaping quotes.
     * 
     * @param reader
     * @throws IOException
     */
    protected String parse(Reader reader) throws IOException {
      if (!reader.markSupported()) {
        reader = new BufferedReader(reader);
      }
      StringWriter sw = new StringWriter();
      startScript(sw);
      int c;
      while ((c = reader.read()) != -1) {
        if (c == '<') {
          reader.mark(1);
          c = reader.read();
          if (c != '%') {
            sw.write('<');
            reader.reset(); 
            continue;
          } 
          reader.mark(1);
          c = reader.read();
          if (c == '=') groovyExpression(reader, sw);
          else {
            reader.reset();
            groovySection(reader, sw);
          }
          continue; // at least '<' is consumed ... read next chars.
        }
        if (c == '\"') sw.write('\\');
        /*
         * Handle raw new line characters.
         */
        if (c == '\n' || c == '\r') {
          if (c == '\r') { // on Windows, "\r\n" is a new line.
            reader.mark(1);
            c = reader.read();
            if (c != '\n') reader.reset();
          }
          sw.write("\\n\");\nout.print(\"");
          continue;
        }
        sw.write(c);
      }
      endScript(sw);
      String result = sw.toString();
      return result;
    }

    private void startScript(StringWriter sw) {
      sw.write("/* Generated by SimpleTemplateEngine */\n");
      sw.write("out.print(\"");
    }

    private void endScript(StringWriter sw) {
      sw.write("\");\n");
    }

    /**
     * Closes the currently open write and writes out the following text as a GString expression until it reaches an end %>.
     * 
     * @param reader
     * @param sw
     * @throws IOException
     */
    private void groovyExpression(Reader reader, StringWriter sw) throws IOException {
      sw.write("\");out.print(\"${");
      int c;
      while ((c = reader.read()) != -1) {
        if (c == '%') {
          c = reader.read();
          if (c == '>') break;
          sw.write('%');
        }
        if (c != '\n' && c != '\r') sw.write(c);
      }
      sw.write("}\");\nout.print(\"");
    }

    /**
     * Closes the currently open write and writes the following text as normal Groovy script code until it reaches an end %>.
     * 
     * @param reader
     * @param sw
     * @throws IOException
     */
    private void groovySection(Reader reader, StringWriter sw) throws IOException {
      sw.write("\");");
      int c;
      while ((c = reader.read()) != -1) {
        if (c == '%') {
          c = reader.read();
          if (c == '>') break;
          sw.write('%');
        }
        /* Don't eat EOL chars in sections - as they are valid instruction separators.
         * See http://jira.codehaus.org/browse/GROOVY-980
         */
        // if (c != '\n' && c != '\r') {
        sw.write(c);
        //}
      }
      sw.write(";\nout.print(\"");
    }
  }
}
