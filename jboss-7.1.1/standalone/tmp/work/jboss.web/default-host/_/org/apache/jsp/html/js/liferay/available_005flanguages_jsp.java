package org.apache.jsp.html.js.liferay;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.LocaleUtil;
import java.util.Locale;

public final class available_005flanguages_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List _jspx_dependants;

  private javax.el.ExpressionFactory _el_expressionfactory;
  private org.apache.tomcat.InstanceManager _jsp_instancemanager;

  public Object getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _el_expressionfactory = _jspxFactory.getJspApplicationContext(getServletConfig().getServletContext()).getExpressionFactory();
    _jsp_instancemanager = org.apache.jasper.runtime.InstanceManagerFactory.getInstanceManager(getServletConfig());
  }

  public void _jspDestroy() {
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;


    try {
      response.setContentType("text/html");
      response.addHeader("X-Powered-By", "JSP/2.2");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");

response.addHeader(HttpHeaders.CONTENT_TYPE, ContentTypes.TEXT_JAVASCRIPT);

String languageId = LanguageUtil.getLanguageId(request);

Locale locale = LocaleUtil.fromLanguageId(languageId);

Locale[] locales = LanguageUtil.getAvailableLocales();

      out.write("\n");
      out.write("\n");
      out.write("AUI.add(\n");
      out.write("\t'portal-available-languages',\n");
      out.write("\tfunction(A) {\n");
      out.write("\t\tvar available = {};\n");
      out.write("\n");
      out.write("\t\tvar direction = {};\n");
      out.write("\n");
      out.write("\t\t");

		for (Locale curLocale : locales) {
			String selLanguageId = LocaleUtil.toLanguageId(curLocale);
		
      out.write("\n");
      out.write("\n");
      out.write("\t\t\tavailable['");
      out.print( selLanguageId );
      out.write("'] = '");
      out.print( curLocale.getDisplayName(locale) );
      out.write("';\n");
      out.write("\t\t\tdirection['");
      out.print( selLanguageId );
      out.write("'] = '");
      out.print( LanguageUtil.get(curLocale, "lang.dir") );
      out.write("';\n");
      out.write("\n");
      out.write("\t\t");

		}
		
      out.write("\n");
      out.write("\n");
      out.write("\t\tLiferay.Language.available = available;\n");
      out.write("\t\tLiferay.Language.direction = direction;\n");
      out.write("\t},\n");
      out.write("\t'',\n");
      out.write("\t{\n");
      out.write("\t\trequires: ['liferay-language']\n");
      out.write("\t}\n");
      out.write(");");
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          try { out.clearBuffer(); } catch (java.io.IOException e) {}
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
