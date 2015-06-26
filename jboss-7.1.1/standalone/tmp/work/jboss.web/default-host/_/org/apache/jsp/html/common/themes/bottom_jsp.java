package org.apache.jsp.html.common.themes;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import com.liferay.counter.service.CounterLocalServiceUtil;
import com.liferay.portal.LocaleException;
import com.liferay.portal.NoSuchLayoutException;
import com.liferay.portal.NoSuchRoleException;
import com.liferay.portal.NoSuchUserException;
import com.liferay.portal.kernel.bean.BeanParamUtil;
import com.liferay.portal.kernel.bean.BeanPropertiesUtil;
import com.liferay.portal.kernel.cal.Recurrence;
import com.liferay.portal.kernel.captcha.CaptchaMaxChallengesException;
import com.liferay.portal.kernel.captcha.CaptchaTextException;
import com.liferay.portal.kernel.configuration.Filter;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.DisplayTerms;
import com.liferay.portal.kernel.dao.search.ResultRow;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.dao.search.SearchEntry;
import com.liferay.portal.kernel.dao.search.TextSearchEntry;
import com.liferay.portal.kernel.exception.LocalizedException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.language.LanguageWrapper;
import com.liferay.portal.kernel.language.UnicodeLanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.log.LogUtil;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.plugin.PluginPackage;
import com.liferay.portal.kernel.portlet.DynamicRenderRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletMode;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchContextFactory;
import com.liferay.portal.kernel.search.SearchResultUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.SortFactoryUtil;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.servlet.BrowserSnifferUtil;
import com.liferay.portal.kernel.servlet.BufferCacheServletResponse;
import com.liferay.portal.kernel.servlet.PortalMessages;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.servlet.ServletContextUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.staging.LayoutStagingUtil;
import com.liferay.portal.kernel.template.StringTemplateResource;
import com.liferay.portal.kernel.template.TemplateHandler;
import com.liferay.portal.kernel.template.TemplateHandlerRegistryUtil;
import com.liferay.portal.kernel.upload.LiferayFileItemException;
import com.liferay.portal.kernel.upload.UploadException;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.CalendarUtil;
import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.CookieKeys;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.KeyValuePairComparator;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.MathUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderedProperties;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PrefsParamUtil;
import com.liferay.portal.kernel.util.PropertiesParamUtil;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.ServerDetector;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.SortedArrayList;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringComparator;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portal.kernel.util.UnicodeFormatter;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UniqueList;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.kernel.workflow.WorkflowDefinitionManagerUtil;
import com.liferay.portal.kernel.workflow.WorkflowEngineManagerUtil;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import com.liferay.portal.layoutconfiguration.util.RuntimePageUtil;
import com.liferay.portal.model.*;
import com.liferay.portal.model.impl.*;
import com.liferay.portal.plugin.PluginUtil;
import com.liferay.portal.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.security.auth.AuthTokenUtil;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.ResourceActionsUtil;
import com.liferay.portal.service.*;
import com.liferay.portal.service.permission.GroupPermissionUtil;
import com.liferay.portal.service.permission.LayoutPermissionUtil;
import com.liferay.portal.service.permission.LayoutPrototypePermissionUtil;
import com.liferay.portal.service.permission.LayoutSetPrototypePermissionUtil;
import com.liferay.portal.service.permission.PortalPermissionUtil;
import com.liferay.portal.service.permission.PortletPermissionUtil;
import com.liferay.portal.service.permission.RolePermissionUtil;
import com.liferay.portal.struts.StrutsUtil;
import com.liferay.portal.struts.TilesAttributeUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.upload.LiferayFileItem;
import com.liferay.portal.util.ClassLoaderUtil;
import com.liferay.portal.util.JavaScriptBundleUtil;
import com.liferay.portal.util.LayoutLister;
import com.liferay.portal.util.LayoutView;
import com.liferay.portal.util.Portal;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.PortletCategoryKeys;
import com.liferay.portal.util.PortletKeys;
import com.liferay.portal.util.PrefsPropsUtil;
import com.liferay.portal.util.PropsUtil;
import com.liferay.portal.util.PropsValues;
import com.liferay.portal.util.SessionClicks;
import com.liferay.portal.util.SessionTreeJSClicks;
import com.liferay.portal.util.ShutdownUtil;
import com.liferay.portal.util.WebAppPool;
import com.liferay.portal.util.WebKeys;
import com.liferay.portal.util.comparator.PortletCategoryComparator;
import com.liferay.portal.util.comparator.PortletTitleComparator;
import com.liferay.portal.webserver.WebServerServletTokenUtil;
import com.liferay.portlet.InvokerPortlet;
import com.liferay.portlet.PortalPreferences;
import com.liferay.portlet.PortletConfigFactoryUtil;
import com.liferay.portlet.PortletInstanceFactoryUtil;
import com.liferay.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portlet.PortletResponseImpl;
import com.liferay.portlet.PortletSetupUtil;
import com.liferay.portlet.PortletURLFactoryUtil;
import com.liferay.portlet.PortletURLImpl;
import com.liferay.portlet.PortletURLUtil;
import com.liferay.portlet.RenderParametersPool;
import com.liferay.portlet.RenderRequestFactory;
import com.liferay.portlet.RenderRequestImpl;
import com.liferay.portlet.RenderResponseFactory;
import com.liferay.portlet.RenderResponseImpl;
import com.liferay.portlet.asset.AssetRendererFactoryRegistryUtil;
import com.liferay.portlet.asset.model.AssetCategory;
import com.liferay.portlet.asset.model.AssetEntry;
import com.liferay.portlet.asset.model.AssetRenderer;
import com.liferay.portlet.asset.model.AssetRendererFactory;
import com.liferay.portlet.asset.model.AssetTag;
import com.liferay.portlet.asset.model.AssetVocabulary;
import com.liferay.portlet.asset.service.AssetCategoryLocalServiceUtil;
import com.liferay.portlet.asset.service.AssetCategoryServiceUtil;
import com.liferay.portlet.asset.service.AssetEntryLocalServiceUtil;
import com.liferay.portlet.asset.service.AssetEntryServiceUtil;
import com.liferay.portlet.asset.service.AssetTagLocalServiceUtil;
import com.liferay.portlet.asset.service.AssetTagServiceUtil;
import com.liferay.portlet.asset.service.AssetVocabularyLocalServiceUtil;
import com.liferay.portlet.asset.service.AssetVocabularyServiceUtil;
import com.liferay.portlet.asset.service.persistence.AssetEntryQuery;
import com.liferay.portlet.asset.util.AssetUtil;
import com.liferay.portlet.blogs.model.BlogsEntry;
import com.liferay.portlet.documentlibrary.FileSizeException;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFileEntryConstants;
import com.liferay.portlet.documentlibrary.service.DLAppLocalServiceUtil;
import com.liferay.portlet.documentlibrary.util.DLUtil;
import com.liferay.portlet.documentlibrary.util.DocumentConversionUtil;
import com.liferay.portlet.dynamicdatamapping.NoSuchStructureException;
import com.liferay.portlet.dynamicdatamapping.model.DDMStructure;
import com.liferay.portlet.dynamicdatamapping.model.DDMTemplate;
import com.liferay.portlet.dynamicdatamapping.service.DDMStructureLocalServiceUtil;
import com.liferay.portlet.dynamicdatamapping.service.DDMTemplateLocalServiceUtil;
import com.liferay.portlet.dynamicdatamapping.service.permission.DDMTemplatePermission;
import com.liferay.portlet.expando.model.ExpandoBridge;
import com.liferay.portlet.journal.action.EditArticleAction;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.model.JournalArticleConstants;
import com.liferay.portlet.journal.model.JournalArticleDisplay;
import com.liferay.portlet.journal.search.ArticleSearch;
import com.liferay.portlet.journal.search.ArticleSearchTerms;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portlet.journal.service.JournalArticleServiceUtil;
import com.liferay.portlet.journalcontent.util.JournalContentUtil;
import com.liferay.portlet.messageboards.model.MBMessage;
import com.liferay.portlet.messageboards.service.MBMessageLocalServiceUtil;
import com.liferay.portlet.portletconfiguration.util.PortletConfigurationUtil;
import com.liferay.portlet.portletdisplaytemplate.util.PortletDisplayTemplateUtil;
import com.liferay.portlet.rolesadmin.util.RolesAdminUtil;
import com.liferay.portlet.sites.util.Sites;
import com.liferay.portlet.sites.util.SitesUtil;
import com.liferay.portlet.trash.model.TrashEntry;
import com.liferay.portlet.trash.util.TrashUtil;
import com.liferay.portlet.usergroupsadmin.search.UserGroupDisplayTerms;
import com.liferay.portlet.usergroupsadmin.search.UserGroupSearch;
import com.liferay.portlet.usersadmin.search.GroupSearch;
import com.liferay.portlet.usersadmin.search.GroupSearchTerms;
import com.liferay.portlet.usersadmin.search.OrganizationSearch;
import com.liferay.portlet.usersadmin.search.OrganizationSearchTerms;
import com.liferay.portlet.usersadmin.search.UserSearch;
import com.liferay.portlet.usersadmin.search.UserSearchTerms;
import com.liferay.portlet.usersadmin.util.UsersAdminUtil;
import com.liferay.taglib.util.OutputTag;
import com.liferay.util.ContentUtil;
import com.liferay.util.CreditCard;
import com.liferay.util.Encryptor;
import com.liferay.util.JS;
import com.liferay.util.PKParser;
import com.liferay.util.PwdGenerator;
import com.liferay.util.State;
import com.liferay.util.StateUtil;
import com.liferay.util.log4j.Levels;
import com.liferay.util.portlet.PortletRequestUtil;
import com.liferay.util.xml.XMLFormatter;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Stack;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.portlet.MimeResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletURL;
import javax.portlet.ResourceURL;
import javax.portlet.UnavailableException;
import javax.portlet.ValidatorException;
import javax.portlet.WindowState;
import com.liferay.portal.kernel.monitoring.RequestStatus;
import com.liferay.portal.kernel.monitoring.statistics.DataSample;
import com.liferay.portal.kernel.monitoring.statistics.DataSampleThreadLocal;
import com.liferay.portal.monitoring.statistics.portal.PortalRequestDataSample;
import com.liferay.portal.security.ldap.LDAPSettingsUtil;
import com.liferay.taglib.aui.ScriptTag;
import org.apache.struts.taglib.tiles.ComponentConstants;
import org.apache.struts.tiles.ComponentContext;

public final class bottom_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {


private static final String _LIFERAY_JS_CONSOLE_COLLAPSED_SESSION_CLICKS_KEY = "liferay_js_console_collapsed";

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List _jspx_dependants;

  static {
    _jspx_dependants = new java.util.ArrayList(11);
    _jspx_dependants.add("/html/common/themes/init.jsp");
    _jspx_dependants.add("/html/common/init.jsp");
    _jspx_dependants.add("/html/common/init-ext.jsp");
    _jspx_dependants.add("/html/common/themes/bottom_portlet_resources_css.jspf");
    _jspx_dependants.add("/html/common/themes/bottom_portlet_resources_js.jspf");
    _jspx_dependants.add("/html/common/themes/bottom_js_logging.jspf");
    _jspx_dependants.add("/html/common/themes/bottom_js.jspf");
    _jspx_dependants.add("/html/portal/layout/view/portlet_js.jspf");
    _jspx_dependants.add("/html/common/themes/password_expiring_soon.jspf");
    _jspx_dependants.add("/html/common/themes/session_timeout.jspf");
    _jspx_dependants.add("/html/common/themes/bottom_monitoring.jspf");
  }

  private org.apache.jasper.runtime.TagHandlerPool _005fjspx_005ftagPool_005fliferay_002dtheme_005fdefineObjects_005fnobody;
  private org.apache.jasper.runtime.TagHandlerPool _005fjspx_005ftagPool_005fc_005fif_0026_005ftest;
  private org.apache.jasper.runtime.TagHandlerPool _005fjspx_005ftagPool_005faui_005fscript;
  private org.apache.jasper.runtime.TagHandlerPool _005fjspx_005ftagPool_005faui_005fscript_0026_005fposition;
  private org.apache.jasper.runtime.TagHandlerPool _005fjspx_005ftagPool_005fc_005fchoose;
  private org.apache.jasper.runtime.TagHandlerPool _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest;
  private org.apache.jasper.runtime.TagHandlerPool _005fjspx_005ftagPool_005fc_005fotherwise;
  private org.apache.jasper.runtime.TagHandlerPool _005fjspx_005ftagPool_005faui_005fscript_0026_005fuse;
  private org.apache.jasper.runtime.TagHandlerPool _005fjspx_005ftagPool_005fliferay_002dui_005fmessage_0026_005fkey_005fnobody;
  private org.apache.jasper.runtime.TagHandlerPool _005fjspx_005ftagPool_005fliferay_002dutil_005finclude_0026_005fpage_005fnobody;

  private javax.el.ExpressionFactory _el_expressionfactory;
  private org.apache.tomcat.InstanceManager _jsp_instancemanager;

  public Object getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _005fjspx_005ftagPool_005fliferay_002dtheme_005fdefineObjects_005fnobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _005fjspx_005ftagPool_005fc_005fif_0026_005ftest = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _005fjspx_005ftagPool_005faui_005fscript = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _005fjspx_005ftagPool_005faui_005fscript_0026_005fposition = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _005fjspx_005ftagPool_005fc_005fchoose = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _005fjspx_005ftagPool_005fc_005fotherwise = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _005fjspx_005ftagPool_005faui_005fscript_0026_005fuse = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _005fjspx_005ftagPool_005fliferay_002dui_005fmessage_0026_005fkey_005fnobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _005fjspx_005ftagPool_005fliferay_002dutil_005finclude_0026_005fpage_005fnobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _el_expressionfactory = _jspxFactory.getJspApplicationContext(getServletConfig().getServletContext()).getExpressionFactory();
    _jsp_instancemanager = org.apache.jasper.runtime.InstanceManagerFactory.getInstanceManager(getServletConfig());
  }

  public void _jspDestroy() {
    _005fjspx_005ftagPool_005fliferay_002dtheme_005fdefineObjects_005fnobody.release();
    _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.release();
    _005fjspx_005ftagPool_005faui_005fscript.release();
    _005fjspx_005ftagPool_005faui_005fscript_0026_005fposition.release();
    _005fjspx_005ftagPool_005fc_005fchoose.release();
    _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.release();
    _005fjspx_005ftagPool_005fc_005fotherwise.release();
    _005fjspx_005ftagPool_005faui_005fscript_0026_005fuse.release();
    _005fjspx_005ftagPool_005fliferay_002dui_005fmessage_0026_005fkey_005fnobody.release();
    _005fjspx_005ftagPool_005fliferay_002dutil_005finclude_0026_005fpage_005fnobody.release();
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
      response.setContentType("text/html; charset=UTF-8");
      response.addHeader("X-Powered-By", "JSP/2.2");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write('\n');
      out.write('\n');
      out.write('\n');
      out.write('\n');
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      //  liferay-theme:defineObjects
      com.liferay.taglib.theme.DefineObjectsTag _jspx_th_liferay_002dtheme_005fdefineObjects_005f0 = (com.liferay.taglib.theme.DefineObjectsTag) _005fjspx_005ftagPool_005fliferay_002dtheme_005fdefineObjects_005fnobody.get(com.liferay.taglib.theme.DefineObjectsTag.class);
      _jspx_th_liferay_002dtheme_005fdefineObjects_005f0.setPageContext(_jspx_page_context);
      _jspx_th_liferay_002dtheme_005fdefineObjects_005f0.setParent(null);
      int _jspx_eval_liferay_002dtheme_005fdefineObjects_005f0 = _jspx_th_liferay_002dtheme_005fdefineObjects_005f0.doStartTag();
      if (_jspx_th_liferay_002dtheme_005fdefineObjects_005f0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _005fjspx_005ftagPool_005fliferay_002dtheme_005fdefineObjects_005fnobody.reuse(_jspx_th_liferay_002dtheme_005fdefineObjects_005f0);
        return;
      }
      _005fjspx_005ftagPool_005fliferay_002dtheme_005fdefineObjects_005fnobody.reuse(_jspx_th_liferay_002dtheme_005fdefineObjects_005f0);
      com.liferay.portal.theme.ThemeDisplay themeDisplay = null;
      com.liferay.portal.model.Company company = null;
      com.liferay.portal.model.Account account = null;
      com.liferay.portal.model.User user = null;
      com.liferay.portal.model.User realUser = null;
      com.liferay.portal.model.Contact contact = null;
      com.liferay.portal.model.Layout layout = null;
      java.util.List layouts = null;
      java.lang.Long plid = null;
      com.liferay.portal.model.LayoutTypePortlet layoutTypePortlet = null;
      java.lang.Long scopeGroupId = null;
      com.liferay.portal.security.permission.PermissionChecker permissionChecker = null;
      java.util.Locale locale = null;
      java.util.TimeZone timeZone = null;
      com.liferay.portal.model.Theme theme = null;
      com.liferay.portal.model.ColorScheme colorScheme = null;
      com.liferay.portal.theme.PortletDisplay portletDisplay = null;
      java.lang.Long portletGroupId = null;
      themeDisplay = (com.liferay.portal.theme.ThemeDisplay) _jspx_page_context.findAttribute("themeDisplay");
      company = (com.liferay.portal.model.Company) _jspx_page_context.findAttribute("company");
      account = (com.liferay.portal.model.Account) _jspx_page_context.findAttribute("account");
      user = (com.liferay.portal.model.User) _jspx_page_context.findAttribute("user");
      realUser = (com.liferay.portal.model.User) _jspx_page_context.findAttribute("realUser");
      contact = (com.liferay.portal.model.Contact) _jspx_page_context.findAttribute("contact");
      layout = (com.liferay.portal.model.Layout) _jspx_page_context.findAttribute("layout");
      layouts = (java.util.List) _jspx_page_context.findAttribute("layouts");
      plid = (java.lang.Long) _jspx_page_context.findAttribute("plid");
      layoutTypePortlet = (com.liferay.portal.model.LayoutTypePortlet) _jspx_page_context.findAttribute("layoutTypePortlet");
      scopeGroupId = (java.lang.Long) _jspx_page_context.findAttribute("scopeGroupId");
      permissionChecker = (com.liferay.portal.security.permission.PermissionChecker) _jspx_page_context.findAttribute("permissionChecker");
      locale = (java.util.Locale) _jspx_page_context.findAttribute("locale");
      timeZone = (java.util.TimeZone) _jspx_page_context.findAttribute("timeZone");
      theme = (com.liferay.portal.model.Theme) _jspx_page_context.findAttribute("theme");
      colorScheme = (com.liferay.portal.model.ColorScheme) _jspx_page_context.findAttribute("colorScheme");
      portletDisplay = (com.liferay.portal.theme.PortletDisplay) _jspx_page_context.findAttribute("portletDisplay");
      portletGroupId = (java.lang.Long) _jspx_page_context.findAttribute("portletGroupId");
      out.write('\n');
      out.write('\n');
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write('\n');
      out.write('\n');

List<Portlet> portlets = (List<Portlet>)request.getAttribute(WebKeys.LAYOUT_PORTLETS);

      out.write('\n');
      out.write('\n');
      out.write('\n');
      out.write('\n');
      out.write('\n');
      out.write('\n');
      //  c:if
      com.liferay.taglib.core.IfTag _jspx_th_c_005fif_005f0 = (com.liferay.taglib.core.IfTag) _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get(com.liferay.taglib.core.IfTag.class);
      _jspx_th_c_005fif_005f0.setPageContext(_jspx_page_context);
      _jspx_th_c_005fif_005f0.setParent(null);
      // /html/common/themes/bottom_portlet_resources_css.jspf(17,0) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
      _jspx_th_c_005fif_005f0.setTest( (portlets != null) && !portlets.isEmpty() );
      int _jspx_eval_c_005fif_005f0 = _jspx_th_c_005fif_005f0.doStartTag();
      if (_jspx_eval_c_005fif_005f0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        out.write('\n');
        out.write('\n');
        out.write('	');

	Set<String> portletResourceStaticURLs = (Set<String>)request.getAttribute(WebKeys.PORTLET_RESOURCE_STATIC_URLS);

	if (portletResourceStaticURLs == null) {
		portletResourceStaticURLs = new LinkedHashSet<String>();

		request.setAttribute(WebKeys.PORTLET_RESOURCE_STATIC_URLS, portletResourceStaticURLs);
	}

	for (Portlet curPortlet : portlets) {
		for (String footerPortalCss : curPortlet.getFooterPortalCss()) {
			if (!HttpUtil.hasProtocol(footerPortalCss)) {
				Portlet curRootPortlet = curPortlet.getRootPortlet();

				footerPortalCss = PortalUtil.getStaticResourceURL(request, PortalUtil.getPathContext() + footerPortalCss, curRootPortlet.getTimestamp());
			}

			if (!footerPortalCss.contains(Http.PROTOCOL_DELIMITER)) {
				String cdnBaseURL = themeDisplay.getCDNBaseURL();

				footerPortalCss = cdnBaseURL.concat(footerPortalCss);
			}

			if (!portletResourceStaticURLs.contains(footerPortalCss)) {
				portletResourceStaticURLs.add(footerPortalCss);
	
        out.write("\n");
        out.write("\n");
        out.write("\t\t\t\t<link href=\"");
        out.print( HtmlUtil.escape(footerPortalCss) );
        out.write("\" rel=\"stylesheet\" type=\"text/css\" />\n");
        out.write("\n");
        out.write("\t");

			}
		}
	}

	for (Portlet curPortlet : portlets) {
		for (String footerPortletCss : curPortlet.getFooterPortletCss()) {
			if (!HttpUtil.hasProtocol(footerPortletCss)) {
				Portlet curRootPortlet = curPortlet.getRootPortlet();

				footerPortletCss = PortalUtil.getStaticResourceURL(request, curPortlet.getStaticResourcePath() + footerPortletCss, curRootPortlet.getTimestamp());
			}

			if (!footerPortletCss.contains(Http.PROTOCOL_DELIMITER)) {
				String cdnBaseURL = themeDisplay.getCDNBaseURL();

				footerPortletCss = cdnBaseURL.concat(footerPortletCss);
			}

			if (!portletResourceStaticURLs.contains(footerPortletCss)) {
				portletResourceStaticURLs.add(footerPortletCss);
	
        out.write("\n");
        out.write("\n");
        out.write("\t\t\t\t<link href=\"");
        out.print( HtmlUtil.escape(footerPortletCss) );
        out.write("\" rel=\"stylesheet\" type=\"text/css\" />\n");
        out.write("\n");
        out.write("\t");

			}
		}
	}
	
        out.write('\n');
        out.write('\n');
      }
      if (_jspx_th_c_005fif_005f0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f0);
        return;
      }
      _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f0);
      out.write('\n');
      out.write('\n');
      out.write('\n');
      out.write('\n');
      out.write('\n');
      out.write('\n');
      //  c:if
      com.liferay.taglib.core.IfTag _jspx_th_c_005fif_005f1 = (com.liferay.taglib.core.IfTag) _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get(com.liferay.taglib.core.IfTag.class);
      _jspx_th_c_005fif_005f1.setPageContext(_jspx_page_context);
      _jspx_th_c_005fif_005f1.setParent(null);
      // /html/common/themes/bottom_portlet_resources_js.jspf(17,0) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
      _jspx_th_c_005fif_005f1.setTest( (portlets != null) && !portlets.isEmpty() );
      int _jspx_eval_c_005fif_005f1 = _jspx_th_c_005fif_005f1.doStartTag();
      if (_jspx_eval_c_005fif_005f1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        out.write('\n');
        out.write('\n');
        out.write('	');

	Set<String> portletResourceStaticURLs = (Set<String>)request.getAttribute(WebKeys.PORTLET_RESOURCE_STATIC_URLS);

	if (portletResourceStaticURLs == null) {
		portletResourceStaticURLs = new LinkedHashSet<String>();

		request.setAttribute(WebKeys.PORTLET_RESOURCE_STATIC_URLS, portletResourceStaticURLs);
	}

	for (Portlet curPortlet : portlets) {
		for (String footerPortalJavaScript : curPortlet.getFooterPortalJavaScript()) {
			if (!HttpUtil.hasProtocol(footerPortalJavaScript)) {
				Portlet curRootPortlet = curPortlet.getRootPortlet();

				footerPortalJavaScript = PortalUtil.getStaticResourceURL(request, PortalUtil.getPathContext() + footerPortalJavaScript, curRootPortlet.getTimestamp());
			}

			if (!footerPortalJavaScript.contains(Http.PROTOCOL_DELIMITER)) {
				String cdnBaseURL = themeDisplay.getCDNBaseURL();

				footerPortalJavaScript = cdnBaseURL.concat(footerPortalJavaScript);
			}

			if (!portletResourceStaticURLs.contains(footerPortalJavaScript) && !themeDisplay.isIncludedJs(footerPortalJavaScript)) {
				portletResourceStaticURLs.add(footerPortalJavaScript);
	
        out.write("\n");
        out.write("\n");
        out.write("\t\t\t\t<script src=\"");
        out.print( HtmlUtil.escape(footerPortalJavaScript) );
        out.write("\" type=\"text/javascript\"></script>\n");
        out.write("\n");
        out.write("\t");

			}
		}
	}

	for (Portlet curPortlet : portlets) {
		for (String footerPortletJavaScript : curPortlet.getFooterPortletJavaScript()) {
			if (!HttpUtil.hasProtocol(footerPortletJavaScript)) {
				Portlet curRootPortlet = curPortlet.getRootPortlet();

				footerPortletJavaScript = PortalUtil.getStaticResourceURL(request, curPortlet.getStaticResourcePath() + footerPortletJavaScript, curRootPortlet.getTimestamp());
			}

			if (!footerPortletJavaScript.contains(Http.PROTOCOL_DELIMITER)) {
				String cdnBaseURL = themeDisplay.getCDNBaseURL();

				footerPortletJavaScript = cdnBaseURL.concat(footerPortletJavaScript);
			}

			if (!portletResourceStaticURLs.contains(footerPortletJavaScript)) {
				portletResourceStaticURLs.add(footerPortletJavaScript);
	
        out.write("\n");
        out.write("\n");
        out.write("\t\t\t\t<script src=\"");
        out.print( HtmlUtil.escape(footerPortletJavaScript) );
        out.write("\" type=\"text/javascript\"></script>\n");
        out.write("\n");
        out.write("\t");

			}
		}
	}
	
        out.write('\n');
        out.write('\n');
      }
      if (_jspx_th_c_005fif_005f1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f1);
        return;
      }
      _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f1);
      out.write('\n');
      out.write('\n');
      //  c:if
      com.liferay.taglib.core.IfTag _jspx_th_c_005fif_005f2 = (com.liferay.taglib.core.IfTag) _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get(com.liferay.taglib.core.IfTag.class);
      _jspx_th_c_005fif_005f2.setPageContext(_jspx_page_context);
      _jspx_th_c_005fif_005f2.setParent(null);
      // /html/common/themes/bottom.jsp(31,0) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
      _jspx_th_c_005fif_005f2.setTest( PropsValues.JAVASCRIPT_LOG_ENABLED );
      int _jspx_eval_c_005fif_005f2 = _jspx_th_c_005fif_005f2.doStartTag();
      if (_jspx_eval_c_005fif_005f2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        out.write('\n');
        out.write('	');
        out.write('\n');
        out.write('\n');

String collapsed = HtmlUtil.escapeJS(GetterUtil.getString(SessionClicks.get(request, _LIFERAY_JS_CONSOLE_COLLAPSED_SESSION_CLICKS_KEY, null), "false"));

        out.write("\n");
        out.write("\n");
        out.write("<div id=\"liferayJSConsole\"></div>\n");
        out.write("\n");
        //  aui:script
        com.liferay.taglib.aui.ScriptTag _jspx_th_aui_005fscript_005f0 = (com.liferay.taglib.aui.ScriptTag) _005fjspx_005ftagPool_005faui_005fscript.get(com.liferay.taglib.aui.ScriptTag.class);
        _jspx_th_aui_005fscript_005f0.setPageContext(_jspx_page_context);
        _jspx_th_aui_005fscript_005f0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_005fif_005f2);
        int _jspx_eval_aui_005fscript_005f0 = _jspx_th_aui_005fscript_005f0.doStartTag();
        if (_jspx_eval_aui_005fscript_005f0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
          if (_jspx_eval_aui_005fscript_005f0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
            out = _jspx_page_context.pushBody();
            _jspx_th_aui_005fscript_005f0.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
            _jspx_th_aui_005fscript_005f0.doInitBody();
          }
          do {
            out.write("\n");
            out.write("\tAUI().use(\n");
            out.write("\t\t'console',\n");
            out.write("\t\t'console-filters',\n");
            out.write("\t\t'liferay-store',\n");
            out.write("\t\tfunction(A) {\n");
            out.write("\t\t\tvar maxHeight = '300px';\n");
            out.write("\t\t\tvar autoHeight = 'auto';\n");
            out.write("\n");
            out.write("\t\t\tvar Console = new A.Console(\n");
            out.write("\t\t\t\t{\n");
            out.write("\t\t\t\t\tafter: {\n");
            out.write("\t\t\t\t\t\tcollapsedChange: function(event) {\n");
            out.write("\t\t\t\t\t\t\tvar instance = this;\n");
            out.write("\n");
            out.write("\t\t\t\t\t\t\tvar height = maxHeight;\n");
            out.write("\n");
            out.write("\t\t\t\t\t\t\tif (event.newVal) {\n");
            out.write("\t\t\t\t\t\t\t\theight = autoHeight;\n");
            out.write("\t\t\t\t\t\t\t}\n");
            out.write("\n");
            out.write("\t\t\t\t\t\t\tinstance.set('height', height);\n");
            out.write("\n");
            out.write("\t\t\t\t\t\t\tLiferay.Store('");
            out.print( _LIFERAY_JS_CONSOLE_COLLAPSED_SESSION_CLICKS_KEY );
            out.write("', String(event.newVal));\n");
            out.write("\t\t\t\t\t\t}\n");
            out.write("\t\t\t\t\t},\n");
            out.write("\t\t\t\t\tcollapsed: ");
            out.print( collapsed );
            out.write(",\n");
            out.write("\t\t\t\t\theight: (");
            out.print( collapsed );
            out.write(") ? autoHeight : maxHeight,\n");
            out.write("\t\t\t\t\tlogSource: A.Global,\n");
            out.write("\t\t\t\t\tnewestOnTop: false,\n");
            out.write("\t\t\t\t\tplugins: [A.Plugin.ConsoleFilters],\n");
            out.write("\t\t\t\t\tstrings: {\n");
            out.write("\t\t\t\t\t\tclear: '");
            out.print( UnicodeLanguageUtil.get(pageContext, "clear") );
            out.write("',\n");
            out.write("\t\t\t\t\t\tcollapse: '");
            out.print( UnicodeLanguageUtil.get(pageContext, "collapse") );
            out.write("',\n");
            out.write("\t\t\t\t\t\texpand: '");
            out.print( UnicodeLanguageUtil.get(pageContext, "expand") );
            out.write("',\n");
            out.write("\t\t\t\t\t\tpause: '");
            out.print( UnicodeLanguageUtil.get(pageContext, "pause") );
            out.write("',\n");
            out.write("\t\t\t\t\t\ttitle: '");
            out.print( UnicodeLanguageUtil.get(pageContext, "console") );
            out.write("'\n");
            out.write("\t\t\t\t\t},\n");
            out.write("\t\t\t\t\tstyle: 'block',\n");
            out.write("\t\t\t\t\twidth: '100%'\n");
            out.write("\t\t\t\t}\n");
            out.write("\t\t\t).render('#liferayJSConsole');\n");
            out.write("\n");
            out.write("\t\t\tLiferay.Console = Console;\n");
            out.write("\t\t}\n");
            out.write("\t);\n");
            int evalDoAfterBody = _jspx_th_aui_005fscript_005f0.doAfterBody();
            if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
              break;
          } while (true);
          if (_jspx_eval_aui_005fscript_005f0 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
            out = _jspx_page_context.popBody();
          }
        }
        if (_jspx_th_aui_005fscript_005f0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _005fjspx_005ftagPool_005faui_005fscript.reuse(_jspx_th_aui_005fscript_005f0);
          return;
        }
        _005fjspx_005ftagPool_005faui_005fscript.reuse(_jspx_th_aui_005fscript_005f0);
        out.write('\n');
        out.write('\n');
        out.write('\n');
      }
      if (_jspx_th_c_005fif_005f2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f2);
        return;
      }
      _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f2);
      out.write('\n');
      out.write('\n');
      out.write('\n');
      out.write('\n');
      out.write('\n');
      out.write('\n');
      //  c:if
      com.liferay.taglib.core.IfTag _jspx_th_c_005fif_005f3 = (com.liferay.taglib.core.IfTag) _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get(com.liferay.taglib.core.IfTag.class);
      _jspx_th_c_005fif_005f3.setPageContext(_jspx_page_context);
      _jspx_th_c_005fif_005f3.setParent(null);
      // /html/portal/layout/view/portlet_js.jspf(17,0) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
      _jspx_th_c_005fif_005f3.setTest( (layout != null) && (LayoutPermissionUtil.contains(permissionChecker, layout, ActionKeys.UPDATE)) );
      int _jspx_eval_c_005fif_005f3 = _jspx_th_c_005fif_005f3.doStartTag();
      if (_jspx_eval_c_005fif_005f3 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        out.write('\n');
        out.write('	');
        if (_jspx_meth_aui_005fscript_005f1(_jspx_th_c_005fif_005f3, _jspx_page_context))
          return;
        out.write('\n');
      }
      if (_jspx_th_c_005fif_005f3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f3);
        return;
      }
      _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f3);
      out.write('\n');
      out.write('\n');
      //  aui:script
      com.liferay.taglib.aui.ScriptTag _jspx_th_aui_005fscript_005f2 = (com.liferay.taglib.aui.ScriptTag) _005fjspx_005ftagPool_005faui_005fscript_0026_005fposition.get(com.liferay.taglib.aui.ScriptTag.class);
      _jspx_th_aui_005fscript_005f2.setPageContext(_jspx_page_context);
      _jspx_th_aui_005fscript_005f2.setParent(null);
      // /html/common/themes/bottom_js.jspf(19,0) name = position type = java.lang.String reqTime = true required = false fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
      _jspx_th_aui_005fscript_005f2.setPosition("inline");
      int _jspx_eval_aui_005fscript_005f2 = _jspx_th_aui_005fscript_005f2.doStartTag();
      if (_jspx_eval_aui_005fscript_005f2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        if (_jspx_eval_aui_005fscript_005f2 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
          out = _jspx_page_context.pushBody();
          _jspx_th_aui_005fscript_005f2.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
          _jspx_th_aui_005fscript_005f2.doInitBody();
        }
        do {
          out.write("\n");
          out.write("\tLiferay.Util.addInputFocus();\n");
          out.write("\n");
          out.write("\t");

	String controlPanelCategory = ParamUtil.getString(request, "controlPanelCategory");
	
          out.write('\n');
          out.write('\n');
          out.write('	');
          //  c:choose
          com.liferay.taglib.core.ChooseTag _jspx_th_c_005fchoose_005f0 = (com.liferay.taglib.core.ChooseTag) _005fjspx_005ftagPool_005fc_005fchoose.get(com.liferay.taglib.core.ChooseTag.class);
          _jspx_th_c_005fchoose_005f0.setPageContext(_jspx_page_context);
          _jspx_th_c_005fchoose_005f0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_005fscript_005f2);
          int _jspx_eval_c_005fchoose_005f0 = _jspx_th_c_005fchoose_005f0.doStartTag();
          if (_jspx_eval_c_005fchoose_005f0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            out.write('\n');
            out.write('	');
            out.write('	');
            //  c:when
            com.liferay.taglib.core.WhenTag _jspx_th_c_005fwhen_005f0 = (com.liferay.taglib.core.WhenTag) _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get(com.liferay.taglib.core.WhenTag.class);
            _jspx_th_c_005fwhen_005f0.setPageContext(_jspx_page_context);
            _jspx_th_c_005fwhen_005f0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_005fchoose_005f0);
            // /html/common/themes/bottom_js.jspf(27,2) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
            _jspx_th_c_005fwhen_005f0.setTest( themeDisplay.isStatePopUp() || Validator.isNotNull(controlPanelCategory) );
            int _jspx_eval_c_005fwhen_005f0 = _jspx_th_c_005fwhen_005f0.doStartTag();
            if (_jspx_eval_c_005fwhen_005f0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
              out.write("\n");
              out.write("\t\t\tLiferay.Util.getTop().Liferay.fire(\n");
              out.write("\t\t\t\t'popupReady',\n");
              out.write("\t\t\t\t{\n");
              out.write("\t\t\t\t\tdoc: document,\n");
              out.write("\t\t\t\t\twin: window,\n");
              out.write("\t\t\t\t\twindowName: Liferay.Util.getWindowName()\n");
              out.write("\t\t\t\t}\n");
              out.write("\t\t\t);\n");
              out.write("\t\t");
            }
            if (_jspx_th_c_005fwhen_005f0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
              _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse(_jspx_th_c_005fwhen_005f0);
              return;
            }
            _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse(_jspx_th_c_005fwhen_005f0);
            out.write('\n');
            out.write('	');
            out.write('	');
            //  c:otherwise
            com.liferay.taglib.core.OtherwiseTag _jspx_th_c_005fotherwise_005f0 = (com.liferay.taglib.core.OtherwiseTag) _005fjspx_005ftagPool_005fc_005fotherwise.get(com.liferay.taglib.core.OtherwiseTag.class);
            _jspx_th_c_005fotherwise_005f0.setPageContext(_jspx_page_context);
            _jspx_th_c_005fotherwise_005f0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_005fchoose_005f0);
            int _jspx_eval_c_005fotherwise_005f0 = _jspx_th_c_005fotherwise_005f0.doStartTag();
            if (_jspx_eval_c_005fotherwise_005f0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
              out.write("\n");
              out.write("\n");
              out.write("\t\t\t");

			String scroll = ParamUtil.getString(request, "scroll");
			
              out.write("\n");
              out.write("\n");
              out.write("\t\t\t");
              //  c:if
              com.liferay.taglib.core.IfTag _jspx_th_c_005fif_005f4 = (com.liferay.taglib.core.IfTag) _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get(com.liferay.taglib.core.IfTag.class);
              _jspx_th_c_005fif_005f4.setPageContext(_jspx_page_context);
              _jspx_th_c_005fif_005f4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_005fotherwise_005f0);
              // /html/common/themes/bottom_js.jspf(43,3) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
              _jspx_th_c_005fif_005f4.setTest( Validator.isNotNull(scroll) );
              int _jspx_eval_c_005fif_005f4 = _jspx_th_c_005fif_005f4.doStartTag();
              if (_jspx_eval_c_005fif_005f4 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                out.write("\n");
                out.write("\t\t\t\tLiferay.on(\n");
                out.write("\t\t\t\t\t'allPortletsReady',\n");
                out.write("\t\t\t\t\tfunction(event) {\n");
                out.write("\t\t\t\t\t\tdocument.getElementById('");
                out.print( HtmlUtil.escape(scroll) );
                out.write("').scrollIntoView();\n");
                out.write("\t\t\t\t\t}\n");
                out.write("\t\t\t\t);\n");
                out.write("\t\t\t");
              }
              if (_jspx_th_c_005fif_005f4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f4);
                return;
              }
              _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f4);
              out.write('\n');
              out.write('	');
              out.write('	');
            }
            if (_jspx_th_c_005fotherwise_005f0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
              _005fjspx_005ftagPool_005fc_005fotherwise.reuse(_jspx_th_c_005fotherwise_005f0);
              return;
            }
            _005fjspx_005ftagPool_005fc_005fotherwise.reuse(_jspx_th_c_005fotherwise_005f0);
            out.write('\n');
            out.write('	');
          }
          if (_jspx_th_c_005fchoose_005f0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _005fjspx_005ftagPool_005fc_005fchoose.reuse(_jspx_th_c_005fchoose_005f0);
            return;
          }
          _005fjspx_005ftagPool_005fc_005fchoose.reuse(_jspx_th_c_005fchoose_005f0);
          out.write('\n');
          int evalDoAfterBody = _jspx_th_aui_005fscript_005f2.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
        if (_jspx_eval_aui_005fscript_005f2 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
          out = _jspx_page_context.popBody();
        }
      }
      if (_jspx_th_aui_005fscript_005f2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _005fjspx_005ftagPool_005faui_005fscript_0026_005fposition.reuse(_jspx_th_aui_005fscript_005f2);
        return;
      }
      _005fjspx_005ftagPool_005faui_005fscript_0026_005fposition.reuse(_jspx_th_aui_005fscript_005f2);
      out.write('\n');
      out.write('\n');
      if (_jspx_meth_aui_005fscript_005f3(_jspx_page_context))
        return;
      out.write('\n');
      out.write('\n');

Group group = null;

LayoutRevision layoutRevision = null;

if (layout != null) {
	group = layout.getGroup();

	layoutRevision = LayoutStagingUtil.getLayoutRevision(layout);
}

      out.write('\n');
      out.write('\n');
      //  c:if
      com.liferay.taglib.core.IfTag _jspx_th_c_005fif_005f5 = (com.liferay.taglib.core.IfTag) _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get(com.liferay.taglib.core.IfTag.class);
      _jspx_th_c_005fif_005f5.setPageContext(_jspx_page_context);
      _jspx_th_c_005fif_005f5.setParent(null);
      // /html/common/themes/bottom_js.jspf(81,0) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
      _jspx_th_c_005fif_005f5.setTest( !themeDisplay.isStatePopUp() && !group.isControlPanel() && (layout != null) && (!group.hasStagingGroup() || group.isStagingGroup() || layoutTypePortlet.isCustomizable()) && (GroupPermissionUtil.contains(permissionChecker, scopeGroupId, ActionKeys.ADD_LAYOUT) || LayoutPermissionUtil.contains(permissionChecker, layout, ActionKeys.UPDATE) || (layoutTypePortlet.isCustomizable() && LayoutPermissionUtil.contains(permissionChecker, layout, ActionKeys.CUSTOMIZE))) );
      int _jspx_eval_c_005fif_005f5 = _jspx_th_c_005fif_005f5.doStartTag();
      if (_jspx_eval_c_005fif_005f5 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        out.write('\n');
        out.write('	');
        //  c:if
        com.liferay.taglib.core.IfTag _jspx_th_c_005fif_005f6 = (com.liferay.taglib.core.IfTag) _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get(com.liferay.taglib.core.IfTag.class);
        _jspx_th_c_005fif_005f6.setPageContext(_jspx_page_context);
        _jspx_th_c_005fif_005f6.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_005fif_005f5);
        // /html/common/themes/bottom_js.jspf(82,1) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
        _jspx_th_c_005fif_005f6.setTest( layout.isTypePortlet() );
        int _jspx_eval_c_005fif_005f6 = _jspx_th_c_005fif_005f6.doStartTag();
        if (_jspx_eval_c_005fif_005f6 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
          out.write('\n');
          out.write('	');
          out.write('	');
          //  aui:script
          com.liferay.taglib.aui.ScriptTag _jspx_th_aui_005fscript_005f4 = (com.liferay.taglib.aui.ScriptTag) _005fjspx_005ftagPool_005faui_005fscript.get(com.liferay.taglib.aui.ScriptTag.class);
          _jspx_th_aui_005fscript_005f4.setPageContext(_jspx_page_context);
          _jspx_th_aui_005fscript_005f4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_005fif_005f6);
          int _jspx_eval_aui_005fscript_005f4 = _jspx_th_aui_005fscript_005f4.doStartTag();
          if (_jspx_eval_aui_005fscript_005f4 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            if (_jspx_eval_aui_005fscript_005f4 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
              out = _jspx_page_context.pushBody();
              _jspx_th_aui_005fscript_005f4.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
              _jspx_th_aui_005fscript_005f4.doInitBody();
            }
            do {
              out.write("\n");
              out.write("\t\t\tLiferay.Data.layoutConfig = {\n");
              out.write("\t\t\t\tcontainer: '#main-content',\n");
              out.write("\n");
              out.write("\t\t\t\t");
              //  c:choose
              com.liferay.taglib.core.ChooseTag _jspx_th_c_005fchoose_005f1 = (com.liferay.taglib.core.ChooseTag) _005fjspx_005ftagPool_005fc_005fchoose.get(com.liferay.taglib.core.ChooseTag.class);
              _jspx_th_c_005fchoose_005f1.setPageContext(_jspx_page_context);
              _jspx_th_c_005fchoose_005f1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_005fscript_005f4);
              int _jspx_eval_c_005fchoose_005f1 = _jspx_th_c_005fchoose_005f1.doStartTag();
              if (_jspx_eval_c_005fchoose_005f1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                out.write("\n");
                out.write("\t\t\t\t\t");
                //  c:when
                com.liferay.taglib.core.WhenTag _jspx_th_c_005fwhen_005f1 = (com.liferay.taglib.core.WhenTag) _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get(com.liferay.taglib.core.WhenTag.class);
                _jspx_th_c_005fwhen_005f1.setPageContext(_jspx_page_context);
                _jspx_th_c_005fwhen_005f1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_005fchoose_005f1);
                // /html/common/themes/bottom_js.jspf(88,5) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
                _jspx_th_c_005fwhen_005f1.setTest( !themeDisplay.isFreeformLayout() );
                int _jspx_eval_c_005fwhen_005f1 = _jspx_th_c_005fwhen_005f1.doStartTag();
                if (_jspx_eval_c_005fwhen_005f1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                  out.write("\n");
                  out.write("\t\t\t\t\t\tdropNodes: '.portlet-column',\n");
                  out.write("\t\t\t\t\t");
                }
                if (_jspx_th_c_005fwhen_005f1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse(_jspx_th_c_005fwhen_005f1);
                  return;
                }
                _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse(_jspx_th_c_005fwhen_005f1);
                out.write("\n");
                out.write("\t\t\t\t\t");
                if (_jspx_meth_c_005fotherwise_005f1(_jspx_th_c_005fchoose_005f1, _jspx_page_context))
                  return;
                out.write("\n");
                out.write("\t\t\t\t");
              }
              if (_jspx_th_c_005fchoose_005f1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _005fjspx_005ftagPool_005fc_005fchoose.reuse(_jspx_th_c_005fchoose_005f1);
                return;
              }
              _005fjspx_005ftagPool_005fc_005fchoose.reuse(_jspx_th_c_005fchoose_005f1);
              out.write("\n");
              out.write("\n");
              out.write("\t\t\t\t");
              //  c:choose
              com.liferay.taglib.core.ChooseTag _jspx_th_c_005fchoose_005f2 = (com.liferay.taglib.core.ChooseTag) _005fjspx_005ftagPool_005fc_005fchoose.get(com.liferay.taglib.core.ChooseTag.class);
              _jspx_th_c_005fchoose_005f2.setPageContext(_jspx_page_context);
              _jspx_th_c_005fchoose_005f2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_005fscript_005f4);
              int _jspx_eval_c_005fchoose_005f2 = _jspx_th_c_005fchoose_005f2.doStartTag();
              if (_jspx_eval_c_005fchoose_005f2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                out.write("\n");
                out.write("\t\t\t\t\t");
                //  c:when
                com.liferay.taglib.core.WhenTag _jspx_th_c_005fwhen_005f2 = (com.liferay.taglib.core.WhenTag) _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get(com.liferay.taglib.core.WhenTag.class);
                _jspx_th_c_005fwhen_005f2.setPageContext(_jspx_page_context);
                _jspx_th_c_005fwhen_005f2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_005fchoose_005f2);
                // /html/common/themes/bottom_js.jspf(99,5) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
                _jspx_th_c_005fwhen_005f2.setTest( BrowserSnifferUtil.isMobile(request) );
                int _jspx_eval_c_005fwhen_005f2 = _jspx_th_c_005fwhen_005f2.doStartTag();
                if (_jspx_eval_c_005fwhen_005f2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                  out.write("\n");
                  out.write("\t\t\t\t\t\thandles: Liferay.Data.PORTLET_TOUCH_DRAG_HANDLE_SELECTOR || ['.portlet-title-text'],\n");
                  out.write("\t\t\t\t\t");
                }
                if (_jspx_th_c_005fwhen_005f2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse(_jspx_th_c_005fwhen_005f2);
                  return;
                }
                _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse(_jspx_th_c_005fwhen_005f2);
                out.write("\n");
                out.write("\t\t\t\t\t");
                //  c:when
                com.liferay.taglib.core.WhenTag _jspx_th_c_005fwhen_005f3 = (com.liferay.taglib.core.WhenTag) _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get(com.liferay.taglib.core.WhenTag.class);
                _jspx_th_c_005fwhen_005f3.setPageContext(_jspx_page_context);
                _jspx_th_c_005fwhen_005f3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_005fchoose_005f2);
                // /html/common/themes/bottom_js.jspf(102,5) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
                _jspx_th_c_005fwhen_005f3.setTest( !themeDisplay.isFreeformLayout() );
                int _jspx_eval_c_005fwhen_005f3 = _jspx_th_c_005fwhen_005f3.doStartTag();
                if (_jspx_eval_c_005fwhen_005f3 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                  out.write("\n");
                  out.write("\t\t\t\t\t\thandles: ['.portlet-title', '.portlet-title-default'],\n");
                  out.write("\t\t\t\t\t");
                }
                if (_jspx_th_c_005fwhen_005f3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                  _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse(_jspx_th_c_005fwhen_005f3);
                  return;
                }
                _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse(_jspx_th_c_005fwhen_005f3);
                out.write("\n");
                out.write("\t\t\t\t\t");
                if (_jspx_meth_c_005fotherwise_005f2(_jspx_th_c_005fchoose_005f2, _jspx_page_context))
                  return;
                out.write("\n");
                out.write("\t\t\t\t");
              }
              if (_jspx_th_c_005fchoose_005f2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _005fjspx_005ftagPool_005fc_005fchoose.reuse(_jspx_th_c_005fchoose_005f2);
                return;
              }
              _005fjspx_005ftagPool_005fc_005fchoose.reuse(_jspx_th_c_005fchoose_005f2);
              out.write("\n");
              out.write("\n");
              out.write("\t\t\t\tdisabledDropContainerClass: 'portlet-dropzone-disabled',\n");
              out.write("\t\t\t\tdragNodes: '.portlet-boundary',\n");
              out.write("\t\t\t\tdropContainer: '.portlet-dropzone',\n");
              out.write("\t\t\t\temptyColumnClass: 'empty',\n");
              out.write("\t\t\t\tinvalid: '.portlet-static',\n");
              out.write("\t\t\t\tnestedPortletId: '_");
              out.print( PortletKeys.NESTED_PORTLETS );
              out.write("_INSTANCE',\n");
              out.write("\t\t\t\tportletBoundary: '.portlet-boundary'\n");
              out.write("\t\t\t};\n");
              out.write("\t\t");
              int evalDoAfterBody = _jspx_th_aui_005fscript_005f4.doAfterBody();
              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                break;
            } while (true);
            if (_jspx_eval_aui_005fscript_005f4 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
              out = _jspx_page_context.popBody();
            }
          }
          if (_jspx_th_aui_005fscript_005f4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _005fjspx_005ftagPool_005faui_005fscript.reuse(_jspx_th_aui_005fscript_005f4);
            return;
          }
          _005fjspx_005ftagPool_005faui_005fscript.reuse(_jspx_th_aui_005fscript_005f4);
          out.write("\n");
          out.write("\n");
          out.write("\t\t");
          if (_jspx_meth_aui_005fscript_005f5(_jspx_th_c_005fif_005f6, _jspx_page_context))
            return;
          out.write('\n');
          out.write('	');
        }
        if (_jspx_th_c_005fif_005f6.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f6);
          return;
        }
        _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f6);
        out.write('\n');
        out.write('\n');
        out.write('	');
        //  aui:script
        com.liferay.taglib.aui.ScriptTag _jspx_th_aui_005fscript_005f6 = (com.liferay.taglib.aui.ScriptTag) _005fjspx_005ftagPool_005faui_005fscript_0026_005fuse.get(com.liferay.taglib.aui.ScriptTag.class);
        _jspx_th_aui_005fscript_005f6.setPageContext(_jspx_page_context);
        _jspx_th_aui_005fscript_005f6.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_005fif_005f5);
        // /html/common/themes/bottom_js.jspf(125,1) name = use type = java.lang.String reqTime = true required = false fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
        _jspx_th_aui_005fscript_005f6.setUse("event-move,liferay-navigation");
        int _jspx_eval_aui_005fscript_005f6 = _jspx_th_aui_005fscript_005f6.doStartTag();
        if (_jspx_eval_aui_005fscript_005f6 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
          if (_jspx_eval_aui_005fscript_005f6 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
            out = _jspx_page_context.pushBody();
            _jspx_th_aui_005fscript_005f6.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
            _jspx_th_aui_005fscript_005f6.doInitBody();
          }
          do {
            out.write("\n");
            out.write("\t\tLiferay.once(\n");
            out.write("\t\t\t'initNavigation',\n");
            out.write("\t\t\tfunction() {\n");
            out.write("\t\t\t\tnew Liferay.Navigation(\n");
            out.write("\t\t\t\t\t{\n");
            out.write("\t\t\t\t\t\thasAddLayoutPermission: ");
            out.print( GroupPermissionUtil.contains(permissionChecker, scopeGroupId, ActionKeys.ADD_LAYOUT) );
            out.write(",\n");
            out.write("\t\t\t\t\t\tlayoutIds: [\n");
            out.write("\n");
            out.write("\t\t\t\t\t\t\t");

							for (int i = 0; (layouts != null) && (i < layouts.size()); i++) {
								Layout curLayout = (Layout)layouts.get(i);
							
            out.write("\n");
            out.write("\n");
            out.write("\t\t\t\t\t\t\t\t{\n");
            out.write("\t\t\t\t\t\t\t\t\tdeletable: !A.UA.touch && ");
            out.print( LayoutPermissionUtil.contains(permissionChecker, curLayout, ActionKeys.DELETE) );
            out.write(",\n");
            out.write("\t\t\t\t\t\t\t\t\tid: ");
            out.print( curLayout.getLayoutId() );
            out.write(",\n");
            out.write("\t\t\t\t\t\t\t\t\tsortable: ");
            out.print( GroupPermissionUtil.contains(permissionChecker, scopeGroupId, ActionKeys.MANAGE_LAYOUTS) && SitesUtil.isLayoutSortable(curLayout) );
            out.write(",\n");
            out.write("\t\t\t\t\t\t\t\t\tupdateable: ");
            out.print( LayoutPermissionUtil.contains(permissionChecker, curLayout, ActionKeys.UPDATE) );
            out.write("\n");
            out.write("\t\t\t\t\t\t\t\t},\n");
            out.write("\n");
            out.write("\t\t\t\t\t\t\t");

							}
							
            out.write("\n");
            out.write("\n");
            out.write("\t\t\t\t\t\t\t],\n");
            out.write("\n");
            out.write("\t\t\t\t\t\t");
            //  c:if
            com.liferay.taglib.core.IfTag _jspx_th_c_005fif_005f7 = (com.liferay.taglib.core.IfTag) _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get(com.liferay.taglib.core.IfTag.class);
            _jspx_th_c_005fif_005f7.setPageContext(_jspx_page_context);
            _jspx_th_c_005fif_005f7.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_005fscript_005f6);
            // /html/common/themes/bottom_js.jspf(152,6) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
            _jspx_th_c_005fif_005f7.setTest( layoutRevision != null );
            int _jspx_eval_c_005fif_005f7 = _jspx_th_c_005fif_005f7.doStartTag();
            if (_jspx_eval_c_005fif_005f7 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
              out.write("\n");
              out.write("\t\t\t\t\t\t\tlayoutSetBranchId: '");
              out.print( layoutRevision.getLayoutSetBranchId() );
              out.write("',\n");
              out.write("\t\t\t\t\t\t");
            }
            if (_jspx_th_c_005fif_005f7.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
              _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f7);
              return;
            }
            _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f7);
            out.write("\n");
            out.write("\n");
            out.write("\t\t\t\t\t\tnavBlock: navBlock\n");
            out.write("\t\t\t\t\t}\n");
            out.write("\t\t\t\t);\n");
            out.write("\t\t\t}\n");
            out.write("\t\t);\n");
            out.write("\n");
            out.write("\t\tvar navBlock = A.one(Liferay.Data.NAV_SELECTOR);\n");
            out.write("\n");
            out.write("\t\tif (navBlock) {\n");
            out.write("\t\t\tnavBlock.once(\n");
            out.write("\t\t\t\t['gesturemovestart', 'mousemove'],\n");
            out.write("\t\t\t\tfunction() {\n");
            out.write("\t\t\t\t\tLiferay.fire('initNavigation');\n");
            out.write("\t\t\t\t}\n");
            out.write("\t\t\t);\n");
            out.write("\t\t}\n");
            out.write("\t");
            int evalDoAfterBody = _jspx_th_aui_005fscript_005f6.doAfterBody();
            if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
              break;
          } while (true);
          if (_jspx_eval_aui_005fscript_005f6 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
            out = _jspx_page_context.popBody();
          }
        }
        if (_jspx_th_aui_005fscript_005f6.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _005fjspx_005ftagPool_005faui_005fscript_0026_005fuse.reuse(_jspx_th_aui_005fscript_005f6);
          return;
        }
        _005fjspx_005ftagPool_005faui_005fscript_0026_005fuse.reuse(_jspx_th_aui_005fscript_005f6);
        out.write('\n');
      }
      if (_jspx_th_c_005fif_005f5.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f5);
        return;
      }
      _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f5);
      out.write('\n');
      out.write('\n');
      //  aui:script
      com.liferay.taglib.aui.ScriptTag _jspx_th_aui_005fscript_005f7 = (com.liferay.taglib.aui.ScriptTag) _005fjspx_005ftagPool_005faui_005fscript_0026_005fuse.get(com.liferay.taglib.aui.ScriptTag.class);
      _jspx_th_aui_005fscript_005f7.setPageContext(_jspx_page_context);
      _jspx_th_aui_005fscript_005f7.setParent(null);
      // /html/common/themes/bottom_js.jspf(175,0) name = use type = java.lang.String reqTime = true required = false fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
      _jspx_th_aui_005fscript_005f7.setUse("liferay-menu,liferay-notice,liferay-poller");
      int _jspx_eval_aui_005fscript_005f7 = _jspx_th_aui_005fscript_005f7.doStartTag();
      if (_jspx_eval_aui_005fscript_005f7 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        if (_jspx_eval_aui_005fscript_005f7 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
          out = _jspx_page_context.pushBody();
          _jspx_th_aui_005fscript_005f7.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
          _jspx_th_aui_005fscript_005f7.doInitBody();
        }
        do {
          out.write("\n");
          out.write("\tnew Liferay.Menu();\n");
          out.write("\n");
          out.write("\tvar liferayNotices = Liferay.Data.notices;\n");
          out.write("\n");
          out.write("\tfor (var i = 1; i < liferayNotices.length; i++) {\n");
          out.write("\t\tnew Liferay.Notice(liferayNotices[i]);\n");
          out.write("\t}\n");
          out.write("\n");
          out.write("\t");
          //  c:if
          com.liferay.taglib.core.IfTag _jspx_th_c_005fif_005f8 = (com.liferay.taglib.core.IfTag) _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get(com.liferay.taglib.core.IfTag.class);
          _jspx_th_c_005fif_005f8.setPageContext(_jspx_page_context);
          _jspx_th_c_005fif_005f8.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_005fscript_005f7);
          // /html/common/themes/bottom_js.jspf(184,1) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
          _jspx_th_c_005fif_005f8.setTest( themeDisplay.isSignedIn() );
          int _jspx_eval_c_005fif_005f8 = _jspx_th_c_005fif_005f8.doStartTag();
          if (_jspx_eval_c_005fif_005f8 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            out.write("\n");
            out.write("\t\tLiferay.Poller.init(\n");
            out.write("\t\t\t{\n");
            out.write("\t\t\t\tencryptedUserId: '");
            out.print( Encryptor.encrypt(company.getKeyObj(), String.valueOf(themeDisplay.getUserId())) );
            out.write("',\n");
            out.write("\t\t\t\tsupportsComet: ");
            out.print( ServerDetector.isSupportsComet() );
            out.write("\n");
            out.write("\t\t\t}\n");
            out.write("\t\t);\n");
            out.write("\t");
          }
          if (_jspx_th_c_005fif_005f8.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f8);
            return;
          }
          _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f8);
          out.write('\n');
          int evalDoAfterBody = _jspx_th_aui_005fscript_005f7.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
        if (_jspx_eval_aui_005fscript_005f7 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
          out = _jspx_page_context.popBody();
        }
      }
      if (_jspx_th_aui_005fscript_005f7.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _005fjspx_005ftagPool_005faui_005fscript_0026_005fuse.reuse(_jspx_th_aui_005fscript_005f7);
        return;
      }
      _005fjspx_005ftagPool_005faui_005fscript_0026_005fuse.reuse(_jspx_th_aui_005fscript_005f7);
      out.write('\n');
      out.write('\n');
      out.write('\n');
      out.write('\n');
      //  c:if
      com.liferay.taglib.core.IfTag _jspx_th_c_005fif_005f9 = (com.liferay.taglib.core.IfTag) _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get(com.liferay.taglib.core.IfTag.class);
      _jspx_th_c_005fif_005f9.setPageContext(_jspx_page_context);
      _jspx_th_c_005fif_005f9.setParent(null);
      // /html/common/themes/password_expiring_soon.jspf(17,0) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
      _jspx_th_c_005fif_005f9.setTest( themeDisplay.isSignedIn() && !LDAPSettingsUtil.isPasswordPolicyEnabled(company.getCompanyId()) && UserLocalServiceUtil.isPasswordExpiringSoon(user) );
      int _jspx_eval_c_005fif_005f9 = _jspx_th_c_005fif_005f9.doStartTag();
      if (_jspx_eval_c_005fif_005f9 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        out.write('\n');
        out.write('	');
        if (_jspx_meth_aui_005fscript_005f8(_jspx_th_c_005fif_005f9, _jspx_page_context))
          return;
        out.write('\n');
      }
      if (_jspx_th_c_005fif_005f9.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f9);
        return;
      }
      _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f9);
      out.write('\n');
      out.write('\n');
      out.write('\n');
      out.write('\n');
      //  c:if
      com.liferay.taglib.core.IfTag _jspx_th_c_005fif_005f10 = (com.liferay.taglib.core.IfTag) _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get(com.liferay.taglib.core.IfTag.class);
      _jspx_th_c_005fif_005f10.setPageContext(_jspx_page_context);
      _jspx_th_c_005fif_005f10.setParent(null);
      // /html/common/themes/session_timeout.jspf(17,0) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
      _jspx_th_c_005fif_005f10.setTest( !PropsValues.SESSION_DISABLED );
      int _jspx_eval_c_005fif_005f10 = _jspx_th_c_005fif_005f10.doStartTag();
      if (_jspx_eval_c_005fif_005f10 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        out.write('\n');
        out.write('\n');
        out.write('	');

	String autoRememberMe = CookieKeys.getCookie(request, CookieKeys.REMEMBER_ME);
	
        out.write('\n');
        out.write('\n');
        out.write('	');
        //  c:if
        com.liferay.taglib.core.IfTag _jspx_th_c_005fif_005f11 = (com.liferay.taglib.core.IfTag) _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get(com.liferay.taglib.core.IfTag.class);
        _jspx_th_c_005fif_005f11.setPageContext(_jspx_page_context);
        _jspx_th_c_005fif_005f11.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_005fif_005f10);
        // /html/common/themes/session_timeout.jspf(23,1) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
        _jspx_th_c_005fif_005f11.setTest( !themeDisplay.isSignedIn() || Validator.isNull(autoRememberMe) );
        int _jspx_eval_c_005fif_005f11 = _jspx_th_c_005fif_005f11.doStartTag();
        if (_jspx_eval_c_005fif_005f11 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
          out.write("\n");
          out.write("\n");
          out.write("\t\t");

		int sessionTimeout = PropsValues.SESSION_TIMEOUT;
		int sessionTimeoutMinute = sessionTimeout * (int)Time.MINUTE;
		int sessionTimeoutWarning = PropsValues.SESSION_TIMEOUT_WARNING;

		boolean sessionRedirectOnExpire = PropsValues.SESSION_TIMEOUT_REDIRECT_ON_EXPIRE;
		String sessionRedirectUrl = themeDisplay.getURLHome();

		long companyId = themeDisplay.getCompanyId();

		if (PrefsPropsUtil.getBoolean(companyId, PropsKeys.CAS_AUTH_ENABLED, PropsValues.CAS_AUTH_ENABLED) && PropsValues.CAS_LOGOUT_ON_SESSION_EXPIRATION) {
			sessionRedirectOnExpire = true;
			sessionRedirectUrl = PrefsPropsUtil.getString(companyId, PropsKeys.CAS_LOGOUT_URL, PropsValues.CAS_LOGOUT_URL);
		}
		else if (PrefsPropsUtil.getBoolean(companyId, PropsKeys.OPEN_SSO_AUTH_ENABLED, PropsValues.OPEN_SSO_AUTH_ENABLED) && PropsValues.OPEN_SSO_LOGOUT_ON_SESSION_EXPIRATION) {
			sessionRedirectOnExpire = true;
			sessionRedirectUrl = PrefsPropsUtil.getString(companyId, PropsKeys.OPEN_SSO_LOGOUT_URL, PropsValues.OPEN_SSO_LOGOUT_URL);
		}

		Calendar sessionTimeoutCal = CalendarFactoryUtil.getCalendar(timeZone);

		sessionTimeoutCal.add(Calendar.MILLISECOND, sessionTimeoutMinute);
		
          out.write("\n");
          out.write("\n");
          out.write("\t\t");
          //  aui:script
          com.liferay.taglib.aui.ScriptTag _jspx_th_aui_005fscript_005f9 = (com.liferay.taglib.aui.ScriptTag) _005fjspx_005ftagPool_005faui_005fscript_0026_005fuse.get(com.liferay.taglib.aui.ScriptTag.class);
          _jspx_th_aui_005fscript_005f9.setPageContext(_jspx_page_context);
          _jspx_th_aui_005fscript_005f9.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_005fif_005f11);
          // /html/common/themes/session_timeout.jspf(49,2) name = use type = java.lang.String reqTime = true required = false fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
          _jspx_th_aui_005fscript_005f9.setUse("liferay-session");
          int _jspx_eval_aui_005fscript_005f9 = _jspx_th_aui_005fscript_005f9.doStartTag();
          if (_jspx_eval_aui_005fscript_005f9 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            if (_jspx_eval_aui_005fscript_005f9 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
              out = _jspx_page_context.pushBody();
              _jspx_th_aui_005fscript_005f9.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
              _jspx_th_aui_005fscript_005f9.doInitBody();
            }
            do {
              out.write("\n");
              out.write("\t\t\tLiferay.Session = new Liferay.SessionBase(\n");
              out.write("\t\t\t\t{\n");
              out.write("\t\t\t\t\tautoExtend: ");
              out.print( !themeDisplay.isSignedIn() || PropsValues.SESSION_TIMEOUT_AUTO_EXTEND );
              out.write(",\n");
              out.write("\t\t\t\t\tsessionLength: ");
              out.print( sessionTimeout );
              out.write(",\n");
              out.write("\t\t\t\t\tredirectOnExpire: ");
              out.print( sessionRedirectOnExpire );
              out.write(",\n");
              out.write("\t\t\t\t\tredirectUrl: '");
              out.print( HtmlUtil.escapeJS(sessionRedirectUrl) );
              out.write("',\n");
              out.write("\t\t\t\t\twarningLength: ");
              out.print( sessionTimeoutWarning );
              out.write("\n");
              out.write("\t\t\t\t}\n");
              out.write("\t\t\t);\n");
              out.write("\n");
              out.write("\t\t\t");
              //  c:if
              com.liferay.taglib.core.IfTag _jspx_th_c_005fif_005f12 = (com.liferay.taglib.core.IfTag) _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get(com.liferay.taglib.core.IfTag.class);
              _jspx_th_c_005fif_005f12.setPageContext(_jspx_page_context);
              _jspx_th_c_005fif_005f12.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_005fscript_005f9);
              // /html/common/themes/session_timeout.jspf(60,3) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
              _jspx_th_c_005fif_005f12.setTest( themeDisplay.isSignedIn() && sessionTimeoutWarning > 0 );
              int _jspx_eval_c_005fif_005f12 = _jspx_th_c_005fif_005f12.doStartTag();
              if (_jspx_eval_c_005fif_005f12 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
                out.write("\n");
                out.write("\t\t\t\tLiferay.Session.plug(Liferay.SessionDisplay);\n");
                out.write("\t\t\t");
              }
              if (_jspx_th_c_005fif_005f12.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
                _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f12);
                return;
              }
              _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f12);
              out.write('\n');
              out.write('	');
              out.write('	');
              int evalDoAfterBody = _jspx_th_aui_005fscript_005f9.doAfterBody();
              if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
                break;
            } while (true);
            if (_jspx_eval_aui_005fscript_005f9 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
              out = _jspx_page_context.popBody();
            }
          }
          if (_jspx_th_aui_005fscript_005f9.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _005fjspx_005ftagPool_005faui_005fscript_0026_005fuse.reuse(_jspx_th_aui_005fscript_005f9);
            return;
          }
          _005fjspx_005ftagPool_005faui_005fscript_0026_005fuse.reuse(_jspx_th_aui_005fscript_005f9);
          out.write('\n');
          out.write('	');
        }
        if (_jspx_th_c_005fif_005f11.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f11);
          return;
        }
        _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f11);
        out.write('\n');
      }
      if (_jspx_th_c_005fif_005f10.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f10);
        return;
      }
      _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f10);
      out.write('\n');
      out.write('\n');

ScriptTag.flushScriptData(pageContext);

      out.write('\n');
      out.write('\n');
      out.write('\n');
      out.write('\n');

StringBundler pageBottomSB = OutputTag.getData(request, WebKeys.PAGE_BOTTOM);

      out.write('\n');
      out.write('\n');
      //  c:if
      com.liferay.taglib.core.IfTag _jspx_th_c_005fif_005f13 = (com.liferay.taglib.core.IfTag) _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get(com.liferay.taglib.core.IfTag.class);
      _jspx_th_c_005fif_005f13.setPageContext(_jspx_page_context);
      _jspx_th_c_005fif_005f13.setParent(null);
      // /html/common/themes/bottom.jsp(51,0) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
      _jspx_th_c_005fif_005f13.setTest( pageBottomSB != null );
      int _jspx_eval_c_005fif_005f13 = _jspx_th_c_005fif_005f13.doStartTag();
      if (_jspx_eval_c_005fif_005f13 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        out.write('\n');
        out.write('\n');
        out.write('	');

	pageBottomSB.writeTo(out);
	
        out.write('\n');
        out.write('\n');
      }
      if (_jspx_th_c_005fif_005f13.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f13);
        return;
      }
      _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f13);
      out.write('\n');
      out.write('\n');
      out.write("\n");
      out.write("\n");
      out.write("<script src=\"");
      out.print( HtmlUtil.escape(PortalUtil.getStaticResourceURL(request, themeDisplay.getPathThemeJavaScript() + "/main.js")) );
      out.write("\" type=\"text/javascript\"></script>\n");
      out.write("\n");
      //  c:if
      com.liferay.taglib.core.IfTag _jspx_th_c_005fif_005f14 = (com.liferay.taglib.core.IfTag) _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get(com.liferay.taglib.core.IfTag.class);
      _jspx_th_c_005fif_005f14.setPageContext(_jspx_page_context);
      _jspx_th_c_005fif_005f14.setParent(null);
      // /html/common/themes/bottom.jsp(63,0) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
      _jspx_th_c_005fif_005f14.setTest( layout != null );
      int _jspx_eval_c_005fif_005f14 = _jspx_th_c_005fif_005f14.doStartTag();
      if (_jspx_eval_c_005fif_005f14 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        out.write('\n');
        out.write('\n');
        out.write('	');
        out.write('\n');
        out.write('\n');
        out.write('	');

	LayoutSet layoutSet = themeDisplay.getLayoutSet();

	UnicodeProperties layoutSetSettings = layoutSet.getSettingsProperties();

	UnicodeProperties layoutTypeSettings = layout.getTypeSettingsProperties();
	
        out.write("\n");
        out.write("\n");
        out.write("\t<script type=\"text/javascript\">\n");
        out.write("\t\t// <![CDATA[\n");
        out.write("\t\t\t");
        out.print( GetterUtil.getString(layoutSetSettings.getProperty("javascript")) );
        out.write("\n");
        out.write("\n");
        out.write("\t\t\t");
        out.print( GetterUtil.getString(layoutTypeSettings.getProperty("javascript")) );
        out.write("\n");
        out.write("\t\t// ]]>\n");
        out.write("\t</script>\n");
      }
      if (_jspx_th_c_005fif_005f14.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f14);
        return;
      }
      _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f14);
      out.write('\n');
      out.write('\n');
      //  c:if
      com.liferay.taglib.core.IfTag _jspx_th_c_005fif_005f15 = (com.liferay.taglib.core.IfTag) _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get(com.liferay.taglib.core.IfTag.class);
      _jspx_th_c_005fif_005f15.setPageContext(_jspx_page_context);
      _jspx_th_c_005fif_005f15.setParent(null);
      // /html/common/themes/bottom.jsp(84,0) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
      _jspx_th_c_005fif_005f15.setTest( PropsValues.MONITORING_PORTAL_REQUEST );
      int _jspx_eval_c_005fif_005f15 = _jspx_th_c_005fif_005f15.doStartTag();
      if (_jspx_eval_c_005fif_005f15 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        out.write('\n');
        out.write('	');
        out.write('\n');
        out.write('\n');

PortalRequestDataSample portalRequestDataSample = (PortalRequestDataSample)request.getAttribute(WebKeys.PORTAL_REQUEST_DATA_SAMPLE);

if (portalRequestDataSample != null) {
	portalRequestDataSample.capture(RequestStatus.SUCCESS);

	DataSampleThreadLocal.addDataSample(portalRequestDataSample);
}

        out.write('\n');
        out.write('\n');
        //  c:if
        com.liferay.taglib.core.IfTag _jspx_th_c_005fif_005f16 = (com.liferay.taglib.core.IfTag) _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get(com.liferay.taglib.core.IfTag.class);
        _jspx_th_c_005fif_005f16.setPageContext(_jspx_page_context);
        _jspx_th_c_005fif_005f16.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_005fif_005f15);
        // /html/common/themes/bottom_monitoring.jspf(27,0) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
        _jspx_th_c_005fif_005f16.setTest( PropsValues.MONITORING_SHOW_PER_REQUEST_DATA_SAMPLE );
        int _jspx_eval_c_005fif_005f16 = _jspx_th_c_005fif_005f16.doStartTag();
        if (_jspx_eval_c_005fif_005f16 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
          out.write("\n");
          out.write("\n");
          out.write("\t<!--\n");
          out.write("\t");

	List<DataSample> dataSamples = DataSampleThreadLocal.getDataSamples();
	
          out.write('\n');
          out.write('\n');
          out.write('	');

	for (DataSample dataSample : dataSamples) {
	
          out.write("\n");
          out.write("\n");
          out.write("\t\t");
          out.print( dataSample );
          out.write("<br />\n");
          out.write("\n");
          out.write("\t");

	}
	
          out.write("\n");
          out.write("\t-->\n");
          out.write("\n");
        }
        if (_jspx_th_c_005fif_005f16.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f16);
          return;
        }
        _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f16);
        out.write('\n');
      }
      if (_jspx_th_c_005fif_005f15.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f15);
        return;
      }
      _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f15);
      out.write('\n');
      out.write('\n');
      if (_jspx_meth_liferay_002dutil_005finclude_005f0(_jspx_page_context))
        return;
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

  private boolean _jspx_meth_aui_005fscript_005f1(javax.servlet.jsp.tagext.JspTag _jspx_th_c_005fif_005f3, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  aui:script
    com.liferay.taglib.aui.ScriptTag _jspx_th_aui_005fscript_005f1 = (com.liferay.taglib.aui.ScriptTag) _005fjspx_005ftagPool_005faui_005fscript.get(com.liferay.taglib.aui.ScriptTag.class);
    _jspx_th_aui_005fscript_005f1.setPageContext(_jspx_page_context);
    _jspx_th_aui_005fscript_005f1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_005fif_005f3);
    int _jspx_eval_aui_005fscript_005f1 = _jspx_th_aui_005fscript_005f1.doStartTag();
    if (_jspx_eval_aui_005fscript_005f1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
      if (_jspx_eval_aui_005fscript_005f1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
        out = _jspx_page_context.pushBody();
        _jspx_th_aui_005fscript_005f1.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
        _jspx_th_aui_005fscript_005f1.doInitBody();
      }
      do {
        out.write("\n");
        out.write("\t\tLiferay.provide(\n");
        out.write("\t\t\tLiferay.Portlet,\n");
        out.write("\t\t\t'refreshLayout',\n");
        out.write("\t\t\tfunction(portletBound) {\n");
        out.write("\t\t\t\tif (!portletBound.isStatic) {\n");
        out.write("\t\t\t\t\tLiferay.Layout.refresh(portletBound);\n");
        out.write("\t\t\t\t}\n");
        out.write("\t\t\t},\n");
        out.write("\t\t\t['liferay-layout']\n");
        out.write("\t\t);\n");
        out.write("\t");
        int evalDoAfterBody = _jspx_th_aui_005fscript_005f1.doAfterBody();
        if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
          break;
      } while (true);
      if (_jspx_eval_aui_005fscript_005f1 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
        out = _jspx_page_context.popBody();
      }
    }
    if (_jspx_th_aui_005fscript_005f1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _005fjspx_005ftagPool_005faui_005fscript.reuse(_jspx_th_aui_005fscript_005f1);
      return true;
    }
    _005fjspx_005ftagPool_005faui_005fscript.reuse(_jspx_th_aui_005fscript_005f1);
    return false;
  }

  private boolean _jspx_meth_aui_005fscript_005f3(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  aui:script
    com.liferay.taglib.aui.ScriptTag _jspx_th_aui_005fscript_005f3 = (com.liferay.taglib.aui.ScriptTag) _005fjspx_005ftagPool_005faui_005fscript_0026_005fuse.get(com.liferay.taglib.aui.ScriptTag.class);
    _jspx_th_aui_005fscript_005f3.setPageContext(_jspx_page_context);
    _jspx_th_aui_005fscript_005f3.setParent(null);
    // /html/common/themes/bottom_js.jspf(55,0) name = use type = java.lang.String reqTime = true required = false fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
    _jspx_th_aui_005fscript_005f3.setUse("aui-base");
    int _jspx_eval_aui_005fscript_005f3 = _jspx_th_aui_005fscript_005f3.doStartTag();
    if (_jspx_eval_aui_005fscript_005f3 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
      if (_jspx_eval_aui_005fscript_005f3 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
        out = _jspx_page_context.pushBody();
        _jspx_th_aui_005fscript_005f3.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
        _jspx_th_aui_005fscript_005f3.doInitBody();
      }
      do {
        out.write("\n");
        out.write("\tLiferay.Util.addInputType();\n");
        out.write("\n");
        out.write("\tLiferay.Portlet.ready(\n");
        out.write("\t\tfunction(portletId, node) {\n");
        out.write("\t\t\tLiferay.Util.addInputType(node);\n");
        out.write("\t\t}\n");
        out.write("\t);\n");
        out.write("\n");
        out.write("\tif (A.UA.mobile) {\n");
        out.write("\t\tLiferay.Util.addInputCancel();\n");
        out.write("\t}\n");
        int evalDoAfterBody = _jspx_th_aui_005fscript_005f3.doAfterBody();
        if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
          break;
      } while (true);
      if (_jspx_eval_aui_005fscript_005f3 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
        out = _jspx_page_context.popBody();
      }
    }
    if (_jspx_th_aui_005fscript_005f3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _005fjspx_005ftagPool_005faui_005fscript_0026_005fuse.reuse(_jspx_th_aui_005fscript_005f3);
      return true;
    }
    _005fjspx_005ftagPool_005faui_005fscript_0026_005fuse.reuse(_jspx_th_aui_005fscript_005f3);
    return false;
  }

  private boolean _jspx_meth_c_005fotherwise_005f1(javax.servlet.jsp.tagext.JspTag _jspx_th_c_005fchoose_005f1, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  c:otherwise
    com.liferay.taglib.core.OtherwiseTag _jspx_th_c_005fotherwise_005f1 = (com.liferay.taglib.core.OtherwiseTag) _005fjspx_005ftagPool_005fc_005fotherwise.get(com.liferay.taglib.core.OtherwiseTag.class);
    _jspx_th_c_005fotherwise_005f1.setPageContext(_jspx_page_context);
    _jspx_th_c_005fotherwise_005f1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_005fchoose_005f1);
    int _jspx_eval_c_005fotherwise_005f1 = _jspx_th_c_005fotherwise_005f1.doStartTag();
    if (_jspx_eval_c_005fotherwise_005f1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
      out.write("\n");
      out.write("\t\t\t\t\t\tdropNodes: '.portlet-boundary',\n");
      out.write("\t\t\t\t\t\tfreeForm: true,\n");
      out.write("\t\t\t\t\t\tfreeformPlaceholderClass: 'lfr-freeform-layout-drag-indicator',\n");
      out.write("\t\t\t\t\t");
    }
    if (_jspx_th_c_005fotherwise_005f1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _005fjspx_005ftagPool_005fc_005fotherwise.reuse(_jspx_th_c_005fotherwise_005f1);
      return true;
    }
    _005fjspx_005ftagPool_005fc_005fotherwise.reuse(_jspx_th_c_005fotherwise_005f1);
    return false;
  }

  private boolean _jspx_meth_c_005fotherwise_005f2(javax.servlet.jsp.tagext.JspTag _jspx_th_c_005fchoose_005f2, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  c:otherwise
    com.liferay.taglib.core.OtherwiseTag _jspx_th_c_005fotherwise_005f2 = (com.liferay.taglib.core.OtherwiseTag) _005fjspx_005ftagPool_005fc_005fotherwise.get(com.liferay.taglib.core.OtherwiseTag.class);
    _jspx_th_c_005fotherwise_005f2.setPageContext(_jspx_page_context);
    _jspx_th_c_005fotherwise_005f2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_005fchoose_005f2);
    int _jspx_eval_c_005fotherwise_005f2 = _jspx_th_c_005fotherwise_005f2.doStartTag();
    if (_jspx_eval_c_005fotherwise_005f2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
      out.write("\n");
      out.write("\t\t\t\t\t\thandles: ['.portlet-header-bar', '.portlet-title-default', '.portlet-topper'],\n");
      out.write("\t\t\t\t\t");
    }
    if (_jspx_th_c_005fotherwise_005f2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _005fjspx_005ftagPool_005fc_005fotherwise.reuse(_jspx_th_c_005fotherwise_005f2);
      return true;
    }
    _005fjspx_005ftagPool_005fc_005fotherwise.reuse(_jspx_th_c_005fotherwise_005f2);
    return false;
  }

  private boolean _jspx_meth_aui_005fscript_005f5(javax.servlet.jsp.tagext.JspTag _jspx_th_c_005fif_005f6, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  aui:script
    com.liferay.taglib.aui.ScriptTag _jspx_th_aui_005fscript_005f5 = (com.liferay.taglib.aui.ScriptTag) _005fjspx_005ftagPool_005faui_005fscript_0026_005fuse.get(com.liferay.taglib.aui.ScriptTag.class);
    _jspx_th_aui_005fscript_005f5.setPageContext(_jspx_page_context);
    _jspx_th_aui_005fscript_005f5.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_005fif_005f6);
    // /html/common/themes/bottom_js.jspf(120,2) name = use type = java.lang.String reqTime = true required = false fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
    _jspx_th_aui_005fscript_005f5.setUse("liferay-layout");
    int _jspx_eval_aui_005fscript_005f5 = _jspx_th_aui_005fscript_005f5.doStartTag();
    if (_jspx_eval_aui_005fscript_005f5 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
      if (_jspx_eval_aui_005fscript_005f5 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
        out = _jspx_page_context.pushBody();
        _jspx_th_aui_005fscript_005f5.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
        _jspx_th_aui_005fscript_005f5.doInitBody();
      }
      do {
        out.write("\n");
        out.write("\t\t\t");
        out.write('\n');
        out.write('	');
        out.write('	');
        int evalDoAfterBody = _jspx_th_aui_005fscript_005f5.doAfterBody();
        if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
          break;
      } while (true);
      if (_jspx_eval_aui_005fscript_005f5 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
        out = _jspx_page_context.popBody();
      }
    }
    if (_jspx_th_aui_005fscript_005f5.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _005fjspx_005ftagPool_005faui_005fscript_0026_005fuse.reuse(_jspx_th_aui_005fscript_005f5);
      return true;
    }
    _005fjspx_005ftagPool_005faui_005fscript_0026_005fuse.reuse(_jspx_th_aui_005fscript_005f5);
    return false;
  }

  private boolean _jspx_meth_aui_005fscript_005f8(javax.servlet.jsp.tagext.JspTag _jspx_th_c_005fif_005f9, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  aui:script
    com.liferay.taglib.aui.ScriptTag _jspx_th_aui_005fscript_005f8 = (com.liferay.taglib.aui.ScriptTag) _005fjspx_005ftagPool_005faui_005fscript_0026_005fuse.get(com.liferay.taglib.aui.ScriptTag.class);
    _jspx_th_aui_005fscript_005f8.setPageContext(_jspx_page_context);
    _jspx_th_aui_005fscript_005f8.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_005fif_005f9);
    // /html/common/themes/password_expiring_soon.jspf(18,1) name = use type = java.lang.String reqTime = true required = false fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
    _jspx_th_aui_005fscript_005f8.setUse("liferay-notice");
    int _jspx_eval_aui_005fscript_005f8 = _jspx_th_aui_005fscript_005f8.doStartTag();
    if (_jspx_eval_aui_005fscript_005f8 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
      if (_jspx_eval_aui_005fscript_005f8 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
        out = _jspx_page_context.pushBody();
        _jspx_th_aui_005fscript_005f8.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
        _jspx_th_aui_005fscript_005f8.doInitBody();
      }
      do {
        out.write("\n");
        out.write("\t\tnew Liferay.Notice(\n");
        out.write("\t\t\t{\n");
        out.write("\t\t\t\tcloseText: '");
        if (_jspx_meth_liferay_002dui_005fmessage_005f0(_jspx_th_aui_005fscript_005f8, _jspx_page_context))
          return true;
        out.write("',\n");
        out.write("\t\t\t\tcontent: '");
        if (_jspx_meth_liferay_002dui_005fmessage_005f1(_jspx_th_aui_005fscript_005f8, _jspx_page_context))
          return true;
        out.write("',\n");
        out.write("\t\t\t\ttoggleText: false\n");
        out.write("\t\t\t}\n");
        out.write("\t\t);\n");
        out.write("\t");
        int evalDoAfterBody = _jspx_th_aui_005fscript_005f8.doAfterBody();
        if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
          break;
      } while (true);
      if (_jspx_eval_aui_005fscript_005f8 != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE) {
        out = _jspx_page_context.popBody();
      }
    }
    if (_jspx_th_aui_005fscript_005f8.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _005fjspx_005ftagPool_005faui_005fscript_0026_005fuse.reuse(_jspx_th_aui_005fscript_005f8);
      return true;
    }
    _005fjspx_005ftagPool_005faui_005fscript_0026_005fuse.reuse(_jspx_th_aui_005fscript_005f8);
    return false;
  }

  private boolean _jspx_meth_liferay_002dui_005fmessage_005f0(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_005fscript_005f8, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-ui:message
    com.liferay.taglib.ui.MessageTag _jspx_th_liferay_002dui_005fmessage_005f0 = (com.liferay.taglib.ui.MessageTag) _005fjspx_005ftagPool_005fliferay_002dui_005fmessage_0026_005fkey_005fnobody.get(com.liferay.taglib.ui.MessageTag.class);
    _jspx_th_liferay_002dui_005fmessage_005f0.setPageContext(_jspx_page_context);
    _jspx_th_liferay_002dui_005fmessage_005f0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_005fscript_005f8);
    // /html/common/themes/password_expiring_soon.jspf(21,16) name = key type = null reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
    _jspx_th_liferay_002dui_005fmessage_005f0.setKey("close");
    int _jspx_eval_liferay_002dui_005fmessage_005f0 = _jspx_th_liferay_002dui_005fmessage_005f0.doStartTag();
    if (_jspx_th_liferay_002dui_005fmessage_005f0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _005fjspx_005ftagPool_005fliferay_002dui_005fmessage_0026_005fkey_005fnobody.reuse(_jspx_th_liferay_002dui_005fmessage_005f0);
      return true;
    }
    _005fjspx_005ftagPool_005fliferay_002dui_005fmessage_0026_005fkey_005fnobody.reuse(_jspx_th_liferay_002dui_005fmessage_005f0);
    return false;
  }

  private boolean _jspx_meth_liferay_002dui_005fmessage_005f1(javax.servlet.jsp.tagext.JspTag _jspx_th_aui_005fscript_005f8, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-ui:message
    com.liferay.taglib.ui.MessageTag _jspx_th_liferay_002dui_005fmessage_005f1 = (com.liferay.taglib.ui.MessageTag) _005fjspx_005ftagPool_005fliferay_002dui_005fmessage_0026_005fkey_005fnobody.get(com.liferay.taglib.ui.MessageTag.class);
    _jspx_th_liferay_002dui_005fmessage_005f1.setPageContext(_jspx_page_context);
    _jspx_th_liferay_002dui_005fmessage_005f1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_aui_005fscript_005f8);
    // /html/common/themes/password_expiring_soon.jspf(22,14) name = key type = null reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
    _jspx_th_liferay_002dui_005fmessage_005f1.setKey("warning-your-password-will-expire-soon");
    int _jspx_eval_liferay_002dui_005fmessage_005f1 = _jspx_th_liferay_002dui_005fmessage_005f1.doStartTag();
    if (_jspx_th_liferay_002dui_005fmessage_005f1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _005fjspx_005ftagPool_005fliferay_002dui_005fmessage_0026_005fkey_005fnobody.reuse(_jspx_th_liferay_002dui_005fmessage_005f1);
      return true;
    }
    _005fjspx_005ftagPool_005fliferay_002dui_005fmessage_0026_005fkey_005fnobody.reuse(_jspx_th_liferay_002dui_005fmessage_005f1);
    return false;
  }

  private boolean _jspx_meth_liferay_002dutil_005finclude_005f0(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-util:include
    com.liferay.taglib.util.IncludeTag _jspx_th_liferay_002dutil_005finclude_005f0 = (com.liferay.taglib.util.IncludeTag) _005fjspx_005ftagPool_005fliferay_002dutil_005finclude_0026_005fpage_005fnobody.get(com.liferay.taglib.util.IncludeTag.class);
    _jspx_th_liferay_002dutil_005finclude_005f0.setPageContext(_jspx_page_context);
    _jspx_th_liferay_002dutil_005finclude_005f0.setParent(null);
    // /html/common/themes/bottom.jsp(88,0) name = page type = null reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
    _jspx_th_liferay_002dutil_005finclude_005f0.setPage("/html/common/themes/bottom-ext.jsp");
    int _jspx_eval_liferay_002dutil_005finclude_005f0 = _jspx_th_liferay_002dutil_005finclude_005f0.doStartTag();
    if (_jspx_th_liferay_002dutil_005finclude_005f0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _005fjspx_005ftagPool_005fliferay_002dutil_005finclude_0026_005fpage_005fnobody.reuse(_jspx_th_liferay_002dutil_005finclude_005f0);
      return true;
    }
    _005fjspx_005ftagPool_005fliferay_002dutil_005finclude_0026_005fpage_005fnobody.reuse(_jspx_th_liferay_002dutil_005finclude_005f0);
    return false;
  }
}
