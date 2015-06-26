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

public final class top_005fhead_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {


private static final String _BOTTOM_KEY = "bottom";

private static final String _LEFT_KEY = "left";

private static final String _RIGHT_KEY = "right";

private static final String _SAME_FOR_ALL_KEY = "sameForAll";

private static final String _TOP_KEY = "top";

private static final String _UNIT_KEY = "unit";

private static final String _VALUE_KEY = "value";

private static final Set _unitSet = new HashSet();

static {
	_unitSet.add("px");
	_unitSet.add("em");
	_unitSet.add("%");
}


private String _escapeCssBlock(String css) {
	return StringUtil.replace(
		css,
		new String[] {"<", "expression("},
		new String[] {"\\3c", ""}
	);
}

private static Log _log = LogFactoryUtil.getLog("portal-web.docroot.html.common.themes.top_head_jsp");

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List _jspx_dependants;

  static {
    _jspx_dependants = new java.util.ArrayList(11);
    _jspx_dependants.add("/html/common/themes/init.jsp");
    _jspx_dependants.add("/html/common/init.jsp");
    _jspx_dependants.add("/html/common/init-ext.jsp");
    _jspx_dependants.add("/html/common/themes/top_monitoring.jspf");
    _jspx_dependants.add("/html/common/themes/top_meta.jspf");
    _jspx_dependants.add("/html/common/themes/top_meta-ext.jsp");
    _jspx_dependants.add("/html/common/themes/top_portlet_resources_css.jspf");
    _jspx_dependants.add("/html/common/themes/top_js.jspf");
    _jspx_dependants.add("/html/common/themes/top_js-ext.jspf");
    _jspx_dependants.add("/html/common/themes/top_portlet_resources_js.jspf");
    _jspx_dependants.add("/html/common/themes/portlet_css.jspf");
  }

  private org.apache.jasper.runtime.TagHandlerPool _005fjspx_005ftagPool_005fliferay_002dtheme_005fdefineObjects_005fnobody;
  private org.apache.jasper.runtime.TagHandlerPool _005fjspx_005ftagPool_005fc_005fif_0026_005ftest;
  private org.apache.jasper.runtime.TagHandlerPool _005fjspx_005ftagPool_005fliferay_002dtheme_005fmeta_002dtags_005fnobody;
  private org.apache.jasper.runtime.TagHandlerPool _005fjspx_005ftagPool_005fliferay_002dui_005fmessage_0026_005fkey_005fnobody;
  private org.apache.jasper.runtime.TagHandlerPool _005fjspx_005ftagPool_005fc_005fchoose;
  private org.apache.jasper.runtime.TagHandlerPool _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest;
  private org.apache.jasper.runtime.TagHandlerPool _005fjspx_005ftagPool_005fc_005fotherwise;

  private javax.el.ExpressionFactory _el_expressionfactory;
  private org.apache.tomcat.InstanceManager _jsp_instancemanager;

  public Object getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _005fjspx_005ftagPool_005fliferay_002dtheme_005fdefineObjects_005fnobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _005fjspx_005ftagPool_005fc_005fif_0026_005ftest = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _005fjspx_005ftagPool_005fliferay_002dtheme_005fmeta_002dtags_005fnobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _005fjspx_005ftagPool_005fliferay_002dui_005fmessage_0026_005fkey_005fnobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _005fjspx_005ftagPool_005fc_005fchoose = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _005fjspx_005ftagPool_005fc_005fotherwise = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _el_expressionfactory = _jspxFactory.getJspApplicationContext(getServletConfig().getServletContext()).getExpressionFactory();
    _jsp_instancemanager = org.apache.jasper.runtime.InstanceManagerFactory.getInstanceManager(getServletConfig());
  }

  public void _jspDestroy() {
    _005fjspx_005ftagPool_005fliferay_002dtheme_005fdefineObjects_005fnobody.release();
    _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.release();
    _005fjspx_005ftagPool_005fliferay_002dtheme_005fmeta_002dtags_005fnobody.release();
    _005fjspx_005ftagPool_005fliferay_002dui_005fmessage_0026_005fkey_005fnobody.release();
    _005fjspx_005ftagPool_005fc_005fchoose.release();
    _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.release();
    _005fjspx_005ftagPool_005fc_005fotherwise.release();
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
      //  c:if
      com.liferay.taglib.core.IfTag _jspx_th_c_005fif_005f0 = (com.liferay.taglib.core.IfTag) _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get(com.liferay.taglib.core.IfTag.class);
      _jspx_th_c_005fif_005f0.setPageContext(_jspx_page_context);
      _jspx_th_c_005fif_005f0.setParent(null);
      // /html/common/themes/top_head.jsp(19,0) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
      _jspx_th_c_005fif_005f0.setTest( PropsValues.MONITORING_PORTAL_REQUEST );
      int _jspx_eval_c_005fif_005f0 = _jspx_th_c_005fif_005f0.doStartTag();
      if (_jspx_eval_c_005fif_005f0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        out.write('\n');
        out.write('	');
        out.write('\n');
        out.write('\n');

PortalRequestDataSample portalRequestDataSample = new PortalRequestDataSample(company.getCompanyId(), request.getRemoteUser(), request.getRequestURI(), request.getRequestURL().toString() + ".jsp_display");

portalRequestDataSample.setDescription("Portal Request");

portalRequestDataSample.prepare();

DataSampleThreadLocal.initialize();

request.setAttribute(WebKeys.PORTAL_REQUEST_DATA_SAMPLE, portalRequestDataSample);

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
      //  c:if
      com.liferay.taglib.core.IfTag _jspx_th_c_005fif_005f1 = (com.liferay.taglib.core.IfTag) _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get(com.liferay.taglib.core.IfTag.class);
      _jspx_th_c_005fif_005f1.setPageContext(_jspx_page_context);
      _jspx_th_c_005fif_005f1.setParent(null);
      // /html/common/themes/top_meta.jspf(17,0) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
      _jspx_th_c_005fif_005f1.setTest( BrowserSnifferUtil.isIe(request) );
      int _jspx_eval_c_005fif_005f1 = _jspx_th_c_005fif_005f1.doStartTag();
      if (_jspx_eval_c_005fif_005f1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        out.write("\n");
        out.write("\t<meta content=\"");
        out.print( PropsValues.BROWSER_COMPATIBILITY_IE_VERSIONS );
        out.write("\" http-equiv=\"x-ua-compatible\" />\n");
      }
      if (_jspx_th_c_005fif_005f1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f1);
        return;
      }
      _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f1);
      out.write("\n");
      out.write("\n");
      out.write("<meta content=\"");
      out.print( ContentTypes.TEXT_HTML_UTF8 );
      out.write("\" http-equiv=\"content-type\" />\n");
      out.write("\n");

int refreshRate = ParamUtil.getInteger(request, "refresh_rate");

      out.write('\n');
      out.write('\n');
      //  c:if
      com.liferay.taglib.core.IfTag _jspx_th_c_005fif_005f2 = (com.liferay.taglib.core.IfTag) _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get(com.liferay.taglib.core.IfTag.class);
      _jspx_th_c_005fif_005f2.setPageContext(_jspx_page_context);
      _jspx_th_c_005fif_005f2.setParent(null);
      // /html/common/themes/top_meta.jspf(27,0) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
      _jspx_th_c_005fif_005f2.setTest( refreshRate > 0 );
      int _jspx_eval_c_005fif_005f2 = _jspx_th_c_005fif_005f2.doStartTag();
      if (_jspx_eval_c_005fif_005f2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        out.write("\n");
        out.write("\t<meta content=\"");
        out.print( refreshRate );
        out.write(";\" http-equiv=\"refresh\" />\n");
      }
      if (_jspx_th_c_005fif_005f2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f2);
        return;
      }
      _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f2);
      out.write('\n');
      out.write('\n');

String cacheControl = request.getParameter("cache_control");

      out.write('\n');
      out.write('\n');
      //  c:if
      com.liferay.taglib.core.IfTag _jspx_th_c_005fif_005f3 = (com.liferay.taglib.core.IfTag) _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get(com.liferay.taglib.core.IfTag.class);
      _jspx_th_c_005fif_005f3.setPageContext(_jspx_page_context);
      _jspx_th_c_005fif_005f3.setParent(null);
      // /html/common/themes/top_meta.jspf(35,0) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
      _jspx_th_c_005fif_005f3.setTest( (cacheControl != null) && (cacheControl.equals("0")) );
      int _jspx_eval_c_005fif_005f3 = _jspx_th_c_005fif_005f3.doStartTag();
      if (_jspx_eval_c_005fif_005f3 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        out.write("\n");
        out.write("\t<meta content=\"no-cache\" http-equiv=\"cache-control\" />\n");
        out.write("\t<meta content=\"no-cache\" http-equiv=\"pragma\" />\n");
        out.write("\t<meta content=\"0\" http-equiv=\"expires\" />\n");
      }
      if (_jspx_th_c_005fif_005f3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f3);
        return;
      }
      _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f3);
      out.write('\n');
      out.write('\n');
      if (_jspx_meth_liferay_002dtheme_005fmeta_002dtags_005f0(_jspx_page_context))
        return;
      out.write('\n');
      out.write("\n");
      out.write("\n");
      out.write("<link href=\"");
      out.print( themeDisplay.getPathThemeImages() );
      out.write('/');
      out.print( PropsValues.THEME_SHORTCUT_ICON );
      out.write("\" rel=\"Shortcut Icon\" />\n");
      out.write("\n");
      out.write('\n');
      out.write('\n');

if (!themeDisplay.isSignedIn() && layout.isPublicLayout()) {
	String completeURL = PortalUtil.getCurrentCompleteURL(request);

	String canonicalURL = PortalUtil.getCanonicalURL(completeURL, themeDisplay, layout);

      out.write("\n");
      out.write("\n");
      out.write("\t<link href=\"");
      out.print( HtmlUtil.escapeAttribute(canonicalURL) );
      out.write("\" rel=\"canonical\" />\n");
      out.write("\n");
      out.write("\t");

	Locale[] availableLocales = LanguageUtil.getAvailableLocales(themeDisplay.getSiteGroupId());

	if (availableLocales.length > 1) {
		for (Locale availableLocale : availableLocales) {
	
      out.write("\n");
      out.write("\n");
      out.write("\t\t\t");
      //  c:if
      com.liferay.taglib.core.IfTag _jspx_th_c_005fif_005f4 = (com.liferay.taglib.core.IfTag) _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get(com.liferay.taglib.core.IfTag.class);
      _jspx_th_c_005fif_005f4.setPageContext(_jspx_page_context);
      _jspx_th_c_005fif_005f4.setParent(null);
      // /html/common/themes/top_head.jsp(46,3) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
      _jspx_th_c_005fif_005f4.setTest( availableLocale.equals(LocaleUtil.getDefault()) );
      int _jspx_eval_c_005fif_005f4 = _jspx_th_c_005fif_005f4.doStartTag();
      if (_jspx_eval_c_005fif_005f4 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        out.write("\n");
        out.write("\t\t\t\t<link href=\"");
        out.print( HtmlUtil.escapeAttribute(canonicalURL) );
        out.write("\" hreflang=\"x-default\" rel=\"alternate\" />\n");
        out.write("\t\t\t");
      }
      if (_jspx_th_c_005fif_005f4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f4);
        return;
      }
      _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f4);
      out.write("\n");
      out.write("\n");
      out.write("\t\t\t<link href=\"");
      out.print( HtmlUtil.escapeAttribute(PortalUtil.getAlternateURL(canonicalURL, themeDisplay, availableLocale, layout)) );
      out.write("\" hreflang=\"");
      out.print( LocaleUtil.toW3cLanguageId(availableLocale) );
      out.write("\" rel=\"alternate\" />\n");
      out.write("\n");
      out.write("\t");

		}
	}
	
      out.write('\n');
      out.write('\n');

}

      out.write('\n');
      out.write('\n');
      out.write("\n");
      out.write("\n");
      out.write("<link class=\"lfr-css-file\" href=\"");
      out.print( HtmlUtil.escapeAttribute(PortalUtil.getStaticResourceURL(request, themeDisplay.getPathThemeCss() + "/aui.css")) );
      out.write("\" rel=\"stylesheet\" type=\"text/css\" />\n");
      out.write("\n");
      out.write("<link href=\"");
      out.print( HtmlUtil.escapeAttribute(PortalUtil.getStaticResourceURL(request, themeDisplay.getCDNDynamicResourcesHost() + themeDisplay.getPathContext() + "/html/css/main.css")) );
      out.write("\" rel=\"stylesheet\" type=\"text/css\" />\n");
      out.write("\n");

List<Portlet> portlets = null;

if (layout != null) {
	String ppid = ParamUtil.getString(request, "p_p_id");

	if (ppid.equals(PortletKeys.PORTLET_CONFIGURATION)) {
		if (themeDisplay.isStatePopUp()) {
			portlets = new ArrayList<Portlet>();
		}
		else {
			portlets = layoutTypePortlet.getAllPortlets();
		}

		portlets.add(PortletLocalServiceUtil.getPortletById(company.getCompanyId(), PortletKeys.PORTLET_CONFIGURATION));

		ppid = ParamUtil.getString(request, PortalUtil.getPortletNamespace(ppid) + "portletResource");

		if (Validator.isNotNull(ppid)) {
			Portlet portlet = PortletLocalServiceUtil.getPortletById(company.getCompanyId(), ppid);

			if ((portlet != null) && !portlets.contains(portlet)) {
				portlets.add(portlet);
			}
		}
	}
	else if (layout.isTypeEmbedded() || layout.isTypePortlet()) {
		portlets = layoutTypePortlet.getAllPortlets();

		if (themeDisplay.isStateMaximized() || themeDisplay.isStatePopUp()) {
			if (Validator.isNotNull(ppid)) {
				Portlet portlet = PortletLocalServiceUtil.getPortletById(company.getCompanyId(), ppid);

				if ((portlet != null) && !portlets.contains(portlet)) {
					portlets.add(portlet);
				}
			}
		}
	}
	else if ((layout.isTypeControlPanel() || layout.isTypePanel()) && Validator.isNotNull(ppid)) {
		portlets = new ArrayList<Portlet>();

		Portlet portlet = PortletLocalServiceUtil.getPortletById(company.getCompanyId(), ppid);

		if (portlet != null) {
			portlets.add(portlet);
		}
	}

	request.setAttribute(WebKeys.LAYOUT_PORTLETS, portlets);
}

      out.write('\n');
      out.write('\n');
      out.write('\n');
      out.write('\n');
      out.write('\n');
      out.write('\n');
      //  c:if
      com.liferay.taglib.core.IfTag _jspx_th_c_005fif_005f5 = (com.liferay.taglib.core.IfTag) _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get(com.liferay.taglib.core.IfTag.class);
      _jspx_th_c_005fif_005f5.setPageContext(_jspx_page_context);
      _jspx_th_c_005fif_005f5.setParent(null);
      // /html/common/themes/top_portlet_resources_css.jspf(17,0) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
      _jspx_th_c_005fif_005f5.setTest( (portlets != null) && !portlets.isEmpty() );
      int _jspx_eval_c_005fif_005f5 = _jspx_th_c_005fif_005f5.doStartTag();
      if (_jspx_eval_c_005fif_005f5 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        out.write('\n');
        out.write('\n');
        out.write('	');

	Set<String> portletResourceStaticURLs = (Set<String>)request.getAttribute(WebKeys.PORTLET_RESOURCE_STATIC_URLS);

	if (portletResourceStaticURLs == null) {
		portletResourceStaticURLs = new LinkedHashSet<String>();

		request.setAttribute(WebKeys.PORTLET_RESOURCE_STATIC_URLS, portletResourceStaticURLs);
	}

	for (Portlet curPortlet : portlets) {
		for (String headerPortalCss : curPortlet.getHeaderPortalCss()) {
			if (!HttpUtil.hasProtocol(headerPortalCss)) {
				Portlet curRootPortlet = curPortlet.getRootPortlet();

				headerPortalCss = PortalUtil.getStaticResourceURL(request, PortalUtil.getPathContext() + headerPortalCss, curRootPortlet.getTimestamp());
			}

			if (!headerPortalCss.contains(Http.PROTOCOL_DELIMITER)) {
				String cdnBaseURL = themeDisplay.getCDNBaseURL();

				headerPortalCss = cdnBaseURL.concat(headerPortalCss);
			}

			if (!portletResourceStaticURLs.contains(headerPortalCss)) {
				portletResourceStaticURLs.add(headerPortalCss);
	
        out.write("\n");
        out.write("\n");
        out.write("\t\t\t\t<link href=\"");
        out.print( HtmlUtil.escape(headerPortalCss) );
        out.write("\" rel=\"stylesheet\" type=\"text/css\" />\n");
        out.write("\n");
        out.write("\t");

			}
		}
	}

	for (Portlet curPortlet : portlets) {
		for (String headerPortletCss : curPortlet.getHeaderPortletCss()) {
			if (!HttpUtil.hasProtocol(headerPortletCss)) {
				Portlet curRootPortlet = curPortlet.getRootPortlet();

				headerPortletCss = PortalUtil.getStaticResourceURL(request, curPortlet.getStaticResourcePath() + headerPortletCss, curRootPortlet.getTimestamp());
			}

			if (!headerPortletCss.contains(Http.PROTOCOL_DELIMITER)) {
				String cdnBaseURL = themeDisplay.getCDNBaseURL();

				headerPortletCss = cdnBaseURL.concat(headerPortletCss);
			}

			if (!portletResourceStaticURLs.contains(headerPortletCss)) {
				portletResourceStaticURLs.add(headerPortletCss);
	
        out.write("\n");
        out.write("\n");
        out.write("\t\t\t\t<link href=\"");
        out.print( HtmlUtil.escape(headerPortletCss) );
        out.write("\" rel=\"stylesheet\" type=\"text/css\" />\n");
        out.write("\n");
        out.write("\t");

			}
		}
	}
	
        out.write('\n');
        out.write('\n');
      }
      if (_jspx_th_c_005fif_005f5.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f5);
        return;
      }
      _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f5);
      out.write('\n');
      out.write('\n');
      out.write('\n');
      out.write('\n');
      out.write("\n");
      out.write("\n");
      out.write("<script type=\"text/javascript\">\n");
      out.write("\t// <![CDATA[\n");
      out.write("\t\tvar Liferay = {\n");
      out.write("\t\t\tBrowser: {\n");
      out.write("\t\t\t\tacceptsGzip: function() {\n");
      out.write("\t\t\t\t\treturn ");
      out.print( BrowserSnifferUtil.acceptsGzip(request) );
      out.write(";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tgetMajorVersion: function() {\n");
      out.write("\t\t\t\t\treturn ");
      out.print( BrowserSnifferUtil.getMajorVersion(request) );
      out.write(";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tgetRevision: function() {\n");
      out.write("\t\t\t\t\treturn \"");
      out.print( BrowserSnifferUtil.getRevision(request) );
      out.write("\";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tgetVersion: function() {\n");
      out.write("\t\t\t\t\treturn \"");
      out.print( BrowserSnifferUtil.getVersion(request) );
      out.write("\";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tisAir: function() {\n");
      out.write("\t\t\t\t\treturn ");
      out.print( BrowserSnifferUtil.isAir(request) );
      out.write(";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tisChrome: function() {\n");
      out.write("\t\t\t\t\treturn ");
      out.print( BrowserSnifferUtil.isChrome(request) );
      out.write(";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tisFirefox: function() {\n");
      out.write("\t\t\t\t\treturn ");
      out.print( BrowserSnifferUtil.isFirefox(request) );
      out.write(";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tisGecko: function() {\n");
      out.write("\t\t\t\t\treturn ");
      out.print( BrowserSnifferUtil.isGecko(request) );
      out.write(";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tisIe: function() {\n");
      out.write("\t\t\t\t\treturn ");
      out.print( BrowserSnifferUtil.isIe(request) );
      out.write(";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tisIphone: function() {\n");
      out.write("\t\t\t\t\treturn ");
      out.print( BrowserSnifferUtil.isIphone(request) );
      out.write(";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tisLinux: function() {\n");
      out.write("\t\t\t\t\treturn ");
      out.print( BrowserSnifferUtil.isLinux(request) );
      out.write(";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tisMac: function() {\n");
      out.write("\t\t\t\t\treturn ");
      out.print( BrowserSnifferUtil.isMac(request) );
      out.write(";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tisMobile: function() {\n");
      out.write("\t\t\t\t\treturn ");
      out.print( BrowserSnifferUtil.isMobile(request) );
      out.write(";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tisMozilla: function() {\n");
      out.write("\t\t\t\t\treturn ");
      out.print( BrowserSnifferUtil.isMozilla(request) );
      out.write(";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tisOpera: function() {\n");
      out.write("\t\t\t\t\treturn ");
      out.print( BrowserSnifferUtil.isOpera(request) );
      out.write(";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tisRtf: function() {\n");
      out.write("\t\t\t\t\treturn ");
      out.print( BrowserSnifferUtil.isRtf(request) );
      out.write(";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tisSafari: function() {\n");
      out.write("\t\t\t\t\treturn ");
      out.print( BrowserSnifferUtil.isSafari(request) );
      out.write(";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tisSun: function() {\n");
      out.write("\t\t\t\t\treturn ");
      out.print( BrowserSnifferUtil.isSun(request) );
      out.write(";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tisWap: function() {\n");
      out.write("\t\t\t\t\treturn ");
      out.print( BrowserSnifferUtil.isWap(request) );
      out.write(";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tisWapXhtml: function() {\n");
      out.write("\t\t\t\t\treturn ");
      out.print( BrowserSnifferUtil.isWapXhtml(request) );
      out.write(";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tisWebKit: function() {\n");
      out.write("\t\t\t\t\treturn ");
      out.print( BrowserSnifferUtil.isWebKit(request) );
      out.write(";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tisWindows: function() {\n");
      out.write("\t\t\t\t\treturn ");
      out.print( BrowserSnifferUtil.isWindows(request) );
      out.write(";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tisWml: function() {\n");
      out.write("\t\t\t\t\treturn ");
      out.print( BrowserSnifferUtil.isWml(request) );
      out.write(";\n");
      out.write("\t\t\t\t}\n");
      out.write("\t\t\t},\n");
      out.write("\n");
      out.write("\t\t\tData: {\n");
      out.write("\t\t\t\tNAV_SELECTOR: '#navigation',\n");
      out.write("\n");
      out.write("\t\t\t\tisCustomizationView: function() {\n");
      out.write("\t\t\t\t\treturn ");
      out.print( (layoutTypePortlet.isCustomizable() && LayoutPermissionUtil.contains(permissionChecker, layout, ActionKeys.CUSTOMIZE)) );
      out.write(";\n");
      out.write("\t\t\t\t},\n");
      out.write("\n");
      out.write("\t\t\t\tnotices: [\n");
      out.write("\t\t\t\t\tnull\n");
      out.write("\n");
      out.write("\t\t\t\t\t");
      //  c:if
      com.liferay.taglib.core.IfTag _jspx_th_c_005fif_005f6 = (com.liferay.taglib.core.IfTag) _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get(com.liferay.taglib.core.IfTag.class);
      _jspx_th_c_005fif_005f6.setPageContext(_jspx_page_context);
      _jspx_th_c_005fif_005f6.setParent(null);
      // /html/common/themes/top_js.jspf(102,5) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
      _jspx_th_c_005fif_005f6.setTest( permissionChecker.isOmniadmin() && PortalUtil.isUpdateAvailable() );
      int _jspx_eval_c_005fif_005f6 = _jspx_th_c_005fif_005f6.doStartTag();
      if (_jspx_eval_c_005fif_005f6 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        out.write("\n");
        out.write("\t\t\t\t\t\t,{\n");
        out.write("\t\t\t\t\t\t\tcontent: '<a class=\"update-available\" href=\"");
        out.print( themeDisplay.getURLUpdateManager() );
        out.write('"');
        out.write('>');
        if (_jspx_meth_liferay_002dui_005fmessage_005f0(_jspx_th_c_005fif_005f6, _jspx_page_context))
          return;
        out.write("</a>',\n");
        out.write("\t\t\t\t\t\t\ttoggleText: false\n");
        out.write("\t\t\t\t\t\t}\n");
        out.write("\t\t\t\t\t");
      }
      if (_jspx_th_c_005fif_005f6.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f6);
        return;
      }
      _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f6);
      out.write("\n");
      out.write("\n");
      out.write("\t\t\t\t\t");
      //  c:if
      com.liferay.taglib.core.IfTag _jspx_th_c_005fif_005f7 = (com.liferay.taglib.core.IfTag) _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get(com.liferay.taglib.core.IfTag.class);
      _jspx_th_c_005fif_005f7.setPageContext(_jspx_page_context);
      _jspx_th_c_005fif_005f7.setParent(null);
      // /html/common/themes/top_js.jspf(109,5) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
      _jspx_th_c_005fif_005f7.setTest( ShutdownUtil.isInProcess() );
      int _jspx_eval_c_005fif_005f7 = _jspx_th_c_005fif_005f7.doStartTag();
      if (_jspx_eval_c_005fif_005f7 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        out.write("\n");
        out.write("\t\t\t\t\t\t,{\n");
        out.write("\t\t\t\t\t\t\tnode: '#lfrShutdownMessage',\n");
        out.write("\t\t\t\t\t\t\ttoggleText: false\n");
        out.write("\t\t\t\t\t\t}\n");
        out.write("\t\t\t\t\t");
      }
      if (_jspx_th_c_005fif_005f7.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f7);
        return;
      }
      _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f7);
      out.write("\n");
      out.write("\t\t\t\t]\n");
      out.write("\t\t\t},\n");
      out.write("\n");
      out.write("\t\t\tThemeDisplay: {\n");
      out.write("\t\t\t\t");
      //  c:if
      com.liferay.taglib.core.IfTag _jspx_th_c_005fif_005f8 = (com.liferay.taglib.core.IfTag) _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get(com.liferay.taglib.core.IfTag.class);
      _jspx_th_c_005fif_005f8.setPageContext(_jspx_page_context);
      _jspx_th_c_005fif_005f8.setParent(null);
      // /html/common/themes/top_js.jspf(119,4) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
      _jspx_th_c_005fif_005f8.setTest( layout != null );
      int _jspx_eval_c_005fif_005f8 = _jspx_th_c_005fif_005f8.doStartTag();
      if (_jspx_eval_c_005fif_005f8 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        out.write("\n");
        out.write("\t\t\t\t\tgetLayoutId: function() {\n");
        out.write("\t\t\t\t\t\treturn \"");
        out.print( layout.getLayoutId() );
        out.write("\";\n");
        out.write("\t\t\t\t\t},\n");
        out.write("\t\t\t\t\tgetLayoutURL: function() {\n");
        out.write("\t\t\t\t\t\treturn \"");
        out.print( PortalUtil.getLayoutURL(layout, themeDisplay) );
        out.write("\";\n");
        out.write("\t\t\t\t\t},\n");
        out.write("\t\t\t\t\tgetParentLayoutId: function() {\n");
        out.write("\t\t\t\t\t\treturn \"");
        out.print( layout.getParentLayoutId() );
        out.write("\";\n");
        out.write("\t\t\t\t\t},\n");
        out.write("\t\t\t\t\tisPrivateLayout: function() {\n");
        out.write("\t\t\t\t\t\treturn \"");
        out.print( layout.isPrivateLayout() );
        out.write("\";\n");
        out.write("\t\t\t\t\t},\n");
        out.write("\t\t\t\t\tisVirtualLayout: function() {\n");
        out.write("\t\t\t\t\t\treturn ");
        out.print( (layout instanceof VirtualLayout) );
        out.write(";\n");
        out.write("\t\t\t\t\t},\n");
        out.write("\t\t\t\t");
      }
      if (_jspx_th_c_005fif_005f8.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f8);
        return;
      }
      _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f8);
      out.write("\n");
      out.write("\n");
      out.write("\t\t\t\tgetBCP47LanguageId: function() {\n");
      out.write("\t\t\t\t\treturn \"");
      out.print( LanguageUtil.getBCP47LanguageId(request) );
      out.write("\";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tgetCDNBaseURL: function() {\n");
      out.write("\t\t\t\t\treturn \"");
      out.print( themeDisplay.getCDNBaseURL() );
      out.write("\";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tgetCDNDynamicResourcesHost: function() {\n");
      out.write("\t\t\t\t\treturn \"");
      out.print( themeDisplay.getCDNDynamicResourcesHost() );
      out.write("\";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tgetCDNHost: function() {\n");
      out.write("\t\t\t\t\treturn \"");
      out.print( themeDisplay.getCDNHost() );
      out.write("\";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tgetCompanyId: function() {\n");
      out.write("\t\t\t\t\treturn \"");
      out.print( themeDisplay.getCompanyId() );
      out.write("\";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tgetCompanyGroupId: function() {\n");
      out.write("\t\t\t\t\treturn \"");
      out.print( themeDisplay.getCompanyGroupId() );
      out.write("\";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tgetDefaultLanguageId: function() {\n");
      out.write("\t\t\t\t\treturn \"");
      out.print( LocaleUtil.toLanguageId(themeDisplay.getSiteDefaultLocale()) );
      out.write("\";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tgetDoAsUserIdEncoded: function() {\n");
      out.write("\t\t\t\t\treturn \"");
      out.print( UnicodeFormatter.toString(themeDisplay.getDoAsUserId()) );
      out.write("\";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tgetLanguageId: function() {\n");
      out.write("\t\t\t\t\treturn \"");
      out.print( LanguageUtil.getLanguageId(request) );
      out.write("\";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tgetParentGroupId: function() {\n");
      out.write("\t\t\t\t\treturn \"");
      out.print( themeDisplay.getSiteGroupId() );
      out.write("\";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tgetPathContext: function() {\n");
      out.write("\t\t\t\t\treturn \"");
      out.print( themeDisplay.getPathContext() );
      out.write("\";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tgetPathImage: function() {\n");
      out.write("\t\t\t\t\treturn \"");
      out.print( themeDisplay.getPathImage() );
      out.write("\";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tgetPathJavaScript: function() {\n");
      out.write("\t\t\t\t\treturn \"");
      out.print( themeDisplay.getPathJavaScript() );
      out.write("\";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tgetPathMain: function() {\n");
      out.write("\t\t\t\t\treturn \"");
      out.print( themeDisplay.getPathMain() );
      out.write("\";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tgetPathThemeImages: function() {\n");
      out.write("\t\t\t\t\treturn \"");
      out.print( themeDisplay.getPathThemeImages() );
      out.write("\";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tgetPathThemeRoot: function() {\n");
      out.write("\t\t\t\t\treturn \"");
      out.print( themeDisplay.getPathThemeRoot() );
      out.write("\";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tgetPlid: function() {\n");
      out.write("\t\t\t\t\treturn \"");
      out.print( themeDisplay.getPlid() );
      out.write("\";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tgetPortalURL: function() {\n");
      out.write("\t\t\t\t\treturn \"");
      out.print( themeDisplay.getPortalURL() );
      out.write("\";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tgetPortletSetupShowBordersDefault: function() {\n");
      out.write("\t\t\t\t\treturn ");
      out.print( GetterUtil.getString(themeDisplay.getThemeSetting("portlet-setup-show-borders-default"), StringUtil.valueOf(PropsValues.THEME_PORTLET_DECORATE_DEFAULT)) );
      out.write(";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tgetScopeGroupId: function() {\n");
      out.write("\t\t\t\t\treturn \"");
      out.print( themeDisplay.getScopeGroupId() );
      out.write("\";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tgetScopeGroupIdOrLiveGroupId: function() {\n");
      out.write("\t\t\t\t\treturn \"");
      out.print( themeDisplay.getSiteGroupIdOrLiveGroupId() );
      out.write("\";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tgetSessionId: function() {\n");
      out.write("\t\t\t\t\t");
      //  c:choose
      com.liferay.taglib.core.ChooseTag _jspx_th_c_005fchoose_005f0 = (com.liferay.taglib.core.ChooseTag) _005fjspx_005ftagPool_005fc_005fchoose.get(com.liferay.taglib.core.ChooseTag.class);
      _jspx_th_c_005fchoose_005f0.setPageContext(_jspx_page_context);
      _jspx_th_c_005fchoose_005f0.setParent(null);
      int _jspx_eval_c_005fchoose_005f0 = _jspx_th_c_005fchoose_005f0.doStartTag();
      if (_jspx_eval_c_005fchoose_005f0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        out.write("\n");
        out.write("\t\t\t\t\t\t");
        //  c:when
        com.liferay.taglib.core.WhenTag _jspx_th_c_005fwhen_005f0 = (com.liferay.taglib.core.WhenTag) _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get(com.liferay.taglib.core.WhenTag.class);
        _jspx_th_c_005fwhen_005f0.setPageContext(_jspx_page_context);
        _jspx_th_c_005fwhen_005f0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_005fchoose_005f0);
        // /html/common/themes/top_js.jspf(202,6) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
        _jspx_th_c_005fwhen_005f0.setTest( PropsValues.SESSION_ENABLE_URL_WITH_SESSION_ID );
        int _jspx_eval_c_005fwhen_005f0 = _jspx_th_c_005fwhen_005f0.doStartTag();
        if (_jspx_eval_c_005fwhen_005f0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
          out.write("\n");
          out.write("\t\t\t\t\t\t\treturn \"");
          out.print( session.getId() );
          out.write("\";\n");
          out.write("\t\t\t\t\t\t");
        }
        if (_jspx_th_c_005fwhen_005f0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse(_jspx_th_c_005fwhen_005f0);
          return;
        }
        _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse(_jspx_th_c_005fwhen_005f0);
        out.write("\n");
        out.write("\t\t\t\t\t\t");
        if (_jspx_meth_c_005fotherwise_005f0(_jspx_th_c_005fchoose_005f0, _jspx_page_context))
          return;
        out.write("\n");
        out.write("\t\t\t\t\t");
      }
      if (_jspx_th_c_005fchoose_005f0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _005fjspx_005ftagPool_005fc_005fchoose.reuse(_jspx_th_c_005fchoose_005f0);
        return;
      }
      _005fjspx_005ftagPool_005fc_005fchoose.reuse(_jspx_th_c_005fchoose_005f0);
      out.write("\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tgetSiteGroupId: function() {\n");
      out.write("\t\t\t\t\treturn \"");
      out.print( themeDisplay.getSiteGroupId() );
      out.write("\";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tgetURLControlPanel: function() {\n");
      out.write("\t\t\t\t\treturn \"");
      out.print( themeDisplay.getURLControlPanel() );
      out.write("\";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tgetURLHome: function() {\n");
      out.write("\t\t\t\t\treturn \"");
      out.print( HtmlUtil.escapeJS(themeDisplay.getURLHome()) );
      out.write("\";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tgetUserId: function() {\n");
      out.write("\t\t\t\t\treturn \"");
      out.print( themeDisplay.getUserId() );
      out.write("\";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tgetUserName: function() {\n");
      out.write("\t\t\t\t\t");
      //  c:choose
      com.liferay.taglib.core.ChooseTag _jspx_th_c_005fchoose_005f1 = (com.liferay.taglib.core.ChooseTag) _005fjspx_005ftagPool_005fc_005fchoose.get(com.liferay.taglib.core.ChooseTag.class);
      _jspx_th_c_005fchoose_005f1.setPageContext(_jspx_page_context);
      _jspx_th_c_005fchoose_005f1.setParent(null);
      int _jspx_eval_c_005fchoose_005f1 = _jspx_th_c_005fchoose_005f1.doStartTag();
      if (_jspx_eval_c_005fchoose_005f1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        out.write("\n");
        out.write("\t\t\t\t\t\t");
        //  c:when
        com.liferay.taglib.core.WhenTag _jspx_th_c_005fwhen_005f1 = (com.liferay.taglib.core.WhenTag) _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get(com.liferay.taglib.core.WhenTag.class);
        _jspx_th_c_005fwhen_005f1.setPageContext(_jspx_page_context);
        _jspx_th_c_005fwhen_005f1.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_005fchoose_005f1);
        // /html/common/themes/top_js.jspf(224,6) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
        _jspx_th_c_005fwhen_005f1.setTest( themeDisplay.isSignedIn() );
        int _jspx_eval_c_005fwhen_005f1 = _jspx_th_c_005fwhen_005f1.doStartTag();
        if (_jspx_eval_c_005fwhen_005f1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
          out.write("\n");
          out.write("\t\t\t\t\t\t\treturn \"");
          out.print( UnicodeFormatter.toString(user.getFullName()) );
          out.write("\";\n");
          out.write("\t\t\t\t\t\t");
        }
        if (_jspx_th_c_005fwhen_005f1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse(_jspx_th_c_005fwhen_005f1);
          return;
        }
        _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse(_jspx_th_c_005fwhen_005f1);
        out.write("\n");
        out.write("\t\t\t\t\t\t");
        if (_jspx_meth_c_005fotherwise_005f1(_jspx_th_c_005fchoose_005f1, _jspx_page_context))
          return;
        out.write("\n");
        out.write("\t\t\t\t\t");
      }
      if (_jspx_th_c_005fchoose_005f1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _005fjspx_005ftagPool_005fc_005fchoose.reuse(_jspx_th_c_005fchoose_005f1);
        return;
      }
      _005fjspx_005ftagPool_005fc_005fchoose.reuse(_jspx_th_c_005fchoose_005f1);
      out.write("\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tisAddSessionIdToURL: function() {\n");
      out.write("\t\t\t\t\treturn ");
      out.print( themeDisplay.isAddSessionIdToURL() );
      out.write(";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tisFreeformLayout: function() {\n");
      out.write("\t\t\t\t\treturn ");
      out.print( themeDisplay.isFreeformLayout() );
      out.write(";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tisImpersonated: function() {\n");
      out.write("\t\t\t\t\treturn ");
      out.print( themeDisplay.isImpersonated() );
      out.write(";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tisSignedIn: function() {\n");
      out.write("\t\t\t\t\treturn ");
      out.print( themeDisplay.isSignedIn() );
      out.write(";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tisStateExclusive: function() {\n");
      out.write("\t\t\t\t\treturn ");
      out.print( themeDisplay.isStateExclusive() );
      out.write(";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tisStateMaximized: function() {\n");
      out.write("\t\t\t\t\treturn ");
      out.print( themeDisplay.isStateMaximized() );
      out.write(";\n");
      out.write("\t\t\t\t},\n");
      out.write("\t\t\t\tisStatePopUp: function() {\n");
      out.write("\t\t\t\t\treturn ");
      out.print( themeDisplay.isStatePopUp() );
      out.write(";\n");
      out.write("\t\t\t\t}\n");
      out.write("\t\t\t},\n");
      out.write("\n");
      out.write("\t\t\tPropsValues: {\n");
      out.write("\t\t\t\tNTLM_AUTH_ENABLED: ");
      out.print( PrefsPropsUtil.getBoolean(themeDisplay.getCompanyId(), PropsKeys.NTLM_AUTH_ENABLED, PropsValues.NTLM_AUTH_ENABLED) );
      out.write("\n");
      out.write("\t\t\t}\n");
      out.write("\t\t};\n");
      out.write("\n");
      out.write("\t\tvar themeDisplay = Liferay.ThemeDisplay;\n");
      out.write("\n");
      out.write("\t\t");

		long javaScriptLastModified = ServletContextUtil.getLastModified(application, "/html/js/", true);

		String javaScriptRootPath = themeDisplay.getPathContext() + "/html/js";

		String alloyComboPath = PortalUtil.getStaticResourceURL(request, themeDisplay.getCDNDynamicResourcesHost() + themeDisplay.getPathContext() + "/combo/", "minifierType=", javaScriptLastModified);
		
      out.write("\n");
      out.write("\n");
      out.write("\t\tLiferay.AUI = {\n");
      out.write("\t\t\tgetAvailableLangPath: function() {\n");
      out.write("\t\t\t\treturn '");
      out.print( PortalUtil.getStaticResourceURL(request, "available_languages.jsp", javaScriptLastModified) );
      out.write("';\n");
      out.write("\t\t\t},\n");
      out.write("\t\t\tgetCombine: function() {\n");
      out.write("\t\t\t\treturn ");
      out.print( themeDisplay.isThemeJsFastLoad() );
      out.write(";\n");
      out.write("\t\t\t},\n");
      out.write("\t\t\tgetComboPath: function() {\n");
      out.write("\t\t\t\treturn '");
      out.print( alloyComboPath );
      out.write("&';\n");
      out.write("\t\t\t},\n");
      out.write("\t\t\tgetFilter: function() {\n");
      out.write("\t\t\t\t");
      //  c:choose
      com.liferay.taglib.core.ChooseTag _jspx_th_c_005fchoose_005f2 = (com.liferay.taglib.core.ChooseTag) _005fjspx_005ftagPool_005fc_005fchoose.get(com.liferay.taglib.core.ChooseTag.class);
      _jspx_th_c_005fchoose_005f2.setPageContext(_jspx_page_context);
      _jspx_th_c_005fchoose_005f2.setParent(null);
      int _jspx_eval_c_005fchoose_005f2 = _jspx_th_c_005fchoose_005f2.doStartTag();
      if (_jspx_eval_c_005fchoose_005f2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        out.write("\n");
        out.write("\t\t\t\t\t");
        //  c:when
        com.liferay.taglib.core.WhenTag _jspx_th_c_005fwhen_005f2 = (com.liferay.taglib.core.WhenTag) _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get(com.liferay.taglib.core.WhenTag.class);
        _jspx_th_c_005fwhen_005f2.setPageContext(_jspx_page_context);
        _jspx_th_c_005fwhen_005f2.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_005fchoose_005f2);
        // /html/common/themes/top_js.jspf(282,5) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
        _jspx_th_c_005fwhen_005f2.setTest( themeDisplay.isThemeJsFastLoad() );
        int _jspx_eval_c_005fwhen_005f2 = _jspx_th_c_005fwhen_005f2.doStartTag();
        if (_jspx_eval_c_005fwhen_005f2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
          out.write("\n");
          out.write("\t\t\t\t\t\treturn 'min';\n");
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
        // /html/common/themes/top_js.jspf(285,5) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
        _jspx_th_c_005fwhen_005f3.setTest( PropsValues.JAVASCRIPT_LOG_ENABLED );
        int _jspx_eval_c_005fwhen_005f3 = _jspx_th_c_005fwhen_005f3.doStartTag();
        if (_jspx_eval_c_005fwhen_005f3 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
          out.write("\n");
          out.write("\t\t\t\t\t\treturn 'debug';\n");
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
      out.write("\t\t\t},\n");
      out.write("\t\t\tgetJavaScriptRootPath: function() {\n");
      out.write("\t\t\t\treturn '");
      out.print( javaScriptRootPath );
      out.write("';\n");
      out.write("\t\t\t},\n");
      out.write("\t\t\tgetLangPath: function() {\n");
      out.write("\t\t\t\treturn '");
      out.print( PortalUtil.getStaticResourceURL(request, "aui_lang.jsp", javaScriptLastModified) );
      out.write("';\n");
      out.write("\t\t\t}\n");
      out.write("\t\t};\n");
      out.write("\n");
      out.write("\t\tLiferay.authToken = '");
      out.print( AuthTokenUtil.getToken(request) );
      out.write("';\n");
      out.write("\n");
      out.write("\t\t");

		String currentURL = PortalUtil.getCurrentURL(request);
		
      out.write("\n");
      out.write("\n");
      out.write("\t\tLiferay.currentURL = '");
      out.print( HtmlUtil.escapeJS(currentURL) );
      out.write("';\n");
      out.write("\t\tLiferay.currentURLEncoded = '");
      out.print( HttpUtil.encodeURL(currentURL) );
      out.write("';\n");
      out.write("\t// ]]>\n");
      out.write("</script>\n");
      out.write("\n");
      //  c:choose
      com.liferay.taglib.core.ChooseTag _jspx_th_c_005fchoose_005f3 = (com.liferay.taglib.core.ChooseTag) _005fjspx_005ftagPool_005fc_005fchoose.get(com.liferay.taglib.core.ChooseTag.class);
      _jspx_th_c_005fchoose_005f3.setPageContext(_jspx_page_context);
      _jspx_th_c_005fchoose_005f3.setParent(null);
      int _jspx_eval_c_005fchoose_005f3 = _jspx_th_c_005fchoose_005f3.doStartTag();
      if (_jspx_eval_c_005fchoose_005f3 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        out.write('\n');
        out.write('	');
        //  c:when
        com.liferay.taglib.core.WhenTag _jspx_th_c_005fwhen_005f4 = (com.liferay.taglib.core.WhenTag) _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get(com.liferay.taglib.core.WhenTag.class);
        _jspx_th_c_005fwhen_005f4.setPageContext(_jspx_page_context);
        _jspx_th_c_005fwhen_005f4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_005fchoose_005f3);
        // /html/common/themes/top_js.jspf(313,1) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
        _jspx_th_c_005fwhen_005f4.setTest( themeDisplay.isThemeJsFastLoad() );
        int _jspx_eval_c_005fwhen_005f4 = _jspx_th_c_005fwhen_005f4.doStartTag();
        if (_jspx_eval_c_005fwhen_005f4 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
          out.write('\n');
          out.write('	');
          out.write('	');
          //  c:choose
          com.liferay.taglib.core.ChooseTag _jspx_th_c_005fchoose_005f4 = (com.liferay.taglib.core.ChooseTag) _005fjspx_005ftagPool_005fc_005fchoose.get(com.liferay.taglib.core.ChooseTag.class);
          _jspx_th_c_005fchoose_005f4.setPageContext(_jspx_page_context);
          _jspx_th_c_005fchoose_005f4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_005fwhen_005f4);
          int _jspx_eval_c_005fchoose_005f4 = _jspx_th_c_005fchoose_005f4.doStartTag();
          if (_jspx_eval_c_005fchoose_005f4 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            out.write("\n");
            out.write("\t\t\t");
            //  c:when
            com.liferay.taglib.core.WhenTag _jspx_th_c_005fwhen_005f5 = (com.liferay.taglib.core.WhenTag) _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get(com.liferay.taglib.core.WhenTag.class);
            _jspx_th_c_005fwhen_005f5.setPageContext(_jspx_page_context);
            _jspx_th_c_005fwhen_005f5.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_005fchoose_005f4);
            // /html/common/themes/top_js.jspf(315,3) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
            _jspx_th_c_005fwhen_005f5.setTest( themeDisplay.isThemeJsBarebone() );
            int _jspx_eval_c_005fwhen_005f5 = _jspx_th_c_005fwhen_005f5.doStartTag();
            if (_jspx_eval_c_005fwhen_005f5 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
              out.write("\n");
              out.write("\t\t\t\t<script src=\"");
              out.print( HtmlUtil.escape(PortalUtil.getStaticResourceURL(request, themeDisplay.getCDNDynamicResourcesHost() + themeDisplay.getPathJavaScript() + "/barebone.jsp", "minifierBundleId=javascript.barebone.files", javaScriptLastModified)) );
              out.write("\" type=\"text/javascript\"></script>\n");
              out.write("\t\t\t");
            }
            if (_jspx_th_c_005fwhen_005f5.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
              _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse(_jspx_th_c_005fwhen_005f5);
              return;
            }
            _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse(_jspx_th_c_005fwhen_005f5);
            out.write("\n");
            out.write("\t\t\t");
            //  c:otherwise
            com.liferay.taglib.core.OtherwiseTag _jspx_th_c_005fotherwise_005f3 = (com.liferay.taglib.core.OtherwiseTag) _005fjspx_005ftagPool_005fc_005fotherwise.get(com.liferay.taglib.core.OtherwiseTag.class);
            _jspx_th_c_005fotherwise_005f3.setPageContext(_jspx_page_context);
            _jspx_th_c_005fotherwise_005f3.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_005fchoose_005f4);
            int _jspx_eval_c_005fotherwise_005f3 = _jspx_th_c_005fotherwise_005f3.doStartTag();
            if (_jspx_eval_c_005fotherwise_005f3 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
              out.write("\n");
              out.write("\t\t\t\t<script src=\"");
              out.print( HtmlUtil.escape(PortalUtil.getStaticResourceURL(request, themeDisplay.getCDNDynamicResourcesHost() + themeDisplay.getPathJavaScript() + "/everything.jsp", "minifierBundleId=javascript.everything.files", javaScriptLastModified)) );
              out.write("\" type=\"text/javascript\"></script>\n");
              out.write("\t\t\t");
            }
            if (_jspx_th_c_005fotherwise_005f3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
              _005fjspx_005ftagPool_005fc_005fotherwise.reuse(_jspx_th_c_005fotherwise_005f3);
              return;
            }
            _005fjspx_005ftagPool_005fc_005fotherwise.reuse(_jspx_th_c_005fotherwise_005f3);
            out.write('\n');
            out.write('	');
            out.write('	');
          }
          if (_jspx_th_c_005fchoose_005f4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _005fjspx_005ftagPool_005fc_005fchoose.reuse(_jspx_th_c_005fchoose_005f4);
            return;
          }
          _005fjspx_005ftagPool_005fc_005fchoose.reuse(_jspx_th_c_005fchoose_005f4);
          out.write('\n');
          out.write('	');
        }
        if (_jspx_th_c_005fwhen_005f4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse(_jspx_th_c_005fwhen_005f4);
          return;
        }
        _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse(_jspx_th_c_005fwhen_005f4);
        out.write('\n');
        out.write('	');
        //  c:otherwise
        com.liferay.taglib.core.OtherwiseTag _jspx_th_c_005fotherwise_005f4 = (com.liferay.taglib.core.OtherwiseTag) _005fjspx_005ftagPool_005fc_005fotherwise.get(com.liferay.taglib.core.OtherwiseTag.class);
        _jspx_th_c_005fotherwise_005f4.setPageContext(_jspx_page_context);
        _jspx_th_c_005fotherwise_005f4.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_005fchoose_005f3);
        int _jspx_eval_c_005fotherwise_005f4 = _jspx_th_c_005fotherwise_005f4.doStartTag();
        if (_jspx_eval_c_005fotherwise_005f4 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
          out.write("\n");
          out.write("\n");
          out.write("\t\t");

		String path = themeDisplay.getCDNHost().concat(themeDisplay.getPathJavaScript());

		String[] javaScriptFiles = null;

		if (themeDisplay.isThemeJsBarebone()) {
			javaScriptFiles = JavaScriptBundleUtil.getFileNames(PropsKeys.JAVASCRIPT_BAREBONE_FILES);
		}
		else {
			javaScriptFiles = JavaScriptBundleUtil.getFileNames(PropsKeys.JAVASCRIPT_EVERYTHING_FILES);
		}

		for (String javaScriptFile : javaScriptFiles) {
		
          out.write("\n");
          out.write("\n");
          out.write("\t\t\t<script src=\"");
          out.print( path );
          out.write('/');
          out.print( javaScriptFile );
          out.write('?');
          out.write('t');
          out.write('=');
          out.print( javaScriptLastModified );
          out.write("\" type=\"text/javascript\"></script>\n");
          out.write("\n");
          out.write("\t\t");

		}
		
          out.write('\n');
          out.write('\n');
          out.write('	');
        }
        if (_jspx_th_c_005fotherwise_005f4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _005fjspx_005ftagPool_005fc_005fotherwise.reuse(_jspx_th_c_005fotherwise_005f4);
          return;
        }
        _005fjspx_005ftagPool_005fc_005fotherwise.reuse(_jspx_th_c_005fotherwise_005f4);
        out.write('\n');
      }
      if (_jspx_th_c_005fchoose_005f3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _005fjspx_005ftagPool_005fc_005fchoose.reuse(_jspx_th_c_005fchoose_005f3);
        return;
      }
      _005fjspx_005ftagPool_005fc_005fchoose.reuse(_jspx_th_c_005fchoose_005f3);
      out.write("\n");
      out.write("\n");
      out.write("<script type=\"text/javascript\">\n");
      out.write("\t// <![CDATA[\n");
      out.write("\t\t");
      //  c:if
      com.liferay.taglib.core.IfTag _jspx_th_c_005fif_005f9 = (com.liferay.taglib.core.IfTag) _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get(com.liferay.taglib.core.IfTag.class);
      _jspx_th_c_005fif_005f9.setPageContext(_jspx_page_context);
      _jspx_th_c_005fif_005f9.setParent(null);
      // /html/common/themes/top_js.jspf(351,2) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
      _jspx_th_c_005fif_005f9.setTest( (layoutTypePortlet != null) );
      int _jspx_eval_c_005fif_005f9 = _jspx_th_c_005fif_005f9.doStartTag();
      if (_jspx_eval_c_005fif_005f9 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        out.write("\n");
        out.write("\n");
        out.write("\t\t\t");

			List<Portlet> allPortlets = layoutTypePortlet.getAllPortlets(false);

			StringBundler sb = new StringBundler(allPortlets.size() * 2);

			for (Portlet portlet : allPortlets) {
				if (portlet.isActive() && portlet.isReady() && !portlet.isUndeployedPortlet()) {
					sb.append(HtmlUtil.escapeJS(portlet.getPortletId()));
					sb.append("', '");
				}
			}

			if (sb.index() > 0) {
				sb.setIndex(sb.index() - 1);
			}
			
        out.write("\n");
        out.write("\n");
        out.write("\t\t\t");
        //  c:if
        com.liferay.taglib.core.IfTag _jspx_th_c_005fif_005f10 = (com.liferay.taglib.core.IfTag) _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get(com.liferay.taglib.core.IfTag.class);
        _jspx_th_c_005fif_005f10.setPageContext(_jspx_page_context);
        _jspx_th_c_005fif_005f10.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_005fif_005f9);
        // /html/common/themes/top_js.jspf(370,3) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
        _jspx_th_c_005fif_005f10.setTest( (sb.index() > 0) && !layoutTypePortlet.hasStateMax() );
        int _jspx_eval_c_005fif_005f10 = _jspx_th_c_005fif_005f10.doStartTag();
        if (_jspx_eval_c_005fif_005f10 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
          out.write("\n");
          out.write("\t\t\t\tLiferay.Portlet.list = ['");
          out.print( sb.toString() );
          out.write("'];\n");
          out.write("\t\t\t");
        }
        if (_jspx_th_c_005fif_005f10.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f10);
          return;
        }
        _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f10);
        out.write('\n');
        out.write('	');
        out.write('	');
      }
      if (_jspx_th_c_005fif_005f9.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f9);
        return;
      }
      _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f9);
      out.write("\n");
      out.write("\n");
      out.write("\t\t");

		Group group = null;

		if (layout != null) {
			group = layout.getGroup();
		}
		
      out.write("\n");
      out.write("\n");
      out.write("\t\t");
      //  c:if
      com.liferay.taglib.core.IfTag _jspx_th_c_005fif_005f11 = (com.liferay.taglib.core.IfTag) _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get(com.liferay.taglib.core.IfTag.class);
      _jspx_th_c_005fif_005f11.setPageContext(_jspx_page_context);
      _jspx_th_c_005fif_005f11.setParent(null);
      // /html/common/themes/top_js.jspf(383,2) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
      _jspx_th_c_005fif_005f11.setTest( themeDisplay.isSignedIn() );
      int _jspx_eval_c_005fif_005f11 = _jspx_th_c_005fif_005f11.doStartTag();
      if (_jspx_eval_c_005fif_005f11 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        out.write("\n");
        out.write("\t\t\t");
        //  c:choose
        com.liferay.taglib.core.ChooseTag _jspx_th_c_005fchoose_005f5 = (com.liferay.taglib.core.ChooseTag) _005fjspx_005ftagPool_005fc_005fchoose.get(com.liferay.taglib.core.ChooseTag.class);
        _jspx_th_c_005fchoose_005f5.setPageContext(_jspx_page_context);
        _jspx_th_c_005fchoose_005f5.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_005fif_005f11);
        int _jspx_eval_c_005fchoose_005f5 = _jspx_th_c_005fchoose_005f5.doStartTag();
        if (_jspx_eval_c_005fchoose_005f5 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
          out.write("\n");
          out.write("\t\t\t\t");
          //  c:when
          com.liferay.taglib.core.WhenTag _jspx_th_c_005fwhen_005f6 = (com.liferay.taglib.core.WhenTag) _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get(com.liferay.taglib.core.WhenTag.class);
          _jspx_th_c_005fwhen_005f6.setPageContext(_jspx_page_context);
          _jspx_th_c_005fwhen_005f6.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_005fchoose_005f5);
          // /html/common/themes/top_js.jspf(385,4) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
          _jspx_th_c_005fwhen_005f6.setTest( (group != null) && group.isControlPanel() && !LayoutPermissionUtil.contains(permissionChecker, layout, ActionKeys.UPDATE) && !(layoutTypePortlet.isCustomizable() && layoutTypePortlet.isCustomizedView() && LayoutPermissionUtil.contains(permissionChecker, layout, ActionKeys.CUSTOMIZE)) );
          int _jspx_eval_c_005fwhen_005f6 = _jspx_th_c_005fwhen_005f6.doStartTag();
          if (_jspx_eval_c_005fwhen_005f6 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            out.write("\n");
            out.write("\t\t\t\t\tLiferay._editControlsState = 'visible';\n");
            out.write("\t\t\t\t");
          }
          if (_jspx_th_c_005fwhen_005f6.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse(_jspx_th_c_005fwhen_005f6);
            return;
          }
          _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse(_jspx_th_c_005fwhen_005f6);
          out.write("\n");
          out.write("\t\t\t\t");
          //  c:otherwise
          com.liferay.taglib.core.OtherwiseTag _jspx_th_c_005fotherwise_005f5 = (com.liferay.taglib.core.OtherwiseTag) _005fjspx_005ftagPool_005fc_005fotherwise.get(com.liferay.taglib.core.OtherwiseTag.class);
          _jspx_th_c_005fotherwise_005f5.setPageContext(_jspx_page_context);
          _jspx_th_c_005fotherwise_005f5.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_005fchoose_005f5);
          int _jspx_eval_c_005fotherwise_005f5 = _jspx_th_c_005fotherwise_005f5.doStartTag();
          if (_jspx_eval_c_005fotherwise_005f5 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            out.write("\n");
            out.write("\t\t\t\t\tLiferay._editControlsState = '");
            out.print( HtmlUtil.escapeJS(GetterUtil.getString(SessionClicks.get(request, "liferay_toggle_controls", "visible"), "visible")) );
            out.write("';\n");
            out.write("\t\t\t\t");
          }
          if (_jspx_th_c_005fotherwise_005f5.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _005fjspx_005ftagPool_005fc_005fotherwise.reuse(_jspx_th_c_005fotherwise_005f5);
            return;
          }
          _005fjspx_005ftagPool_005fc_005fotherwise.reuse(_jspx_th_c_005fotherwise_005f5);
          out.write("\n");
          out.write("\t\t\t");
        }
        if (_jspx_th_c_005fchoose_005f5.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _005fjspx_005ftagPool_005fc_005fchoose.reuse(_jspx_th_c_005fchoose_005f5);
          return;
        }
        _005fjspx_005ftagPool_005fc_005fchoose.reuse(_jspx_th_c_005fchoose_005f5);
        out.write('\n');
        out.write('	');
        out.write('	');
      }
      if (_jspx_th_c_005fif_005f11.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f11);
        return;
      }
      _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f11);
      out.write("\n");
      out.write("\t// ]]>\n");
      out.write("</script>\n");
      out.write("\n");
      out.write('\n');
      out.write('\n');

UnicodeProperties groupTypeSettings = group.getTypeSettingsProperties();

String[] analyticsTypes = PrefsPropsUtil.getStringArray(company.getCompanyId(), PropsKeys.ADMIN_ANALYTICS_TYPES, StringPool.NEW_LINE);

for (String analyticsType : analyticsTypes) {

      out.write('\n');
      out.write('\n');
      out.write('	');
      //  c:choose
      com.liferay.taglib.core.ChooseTag _jspx_th_c_005fchoose_005f6 = (com.liferay.taglib.core.ChooseTag) _005fjspx_005ftagPool_005fc_005fchoose.get(com.liferay.taglib.core.ChooseTag.class);
      _jspx_th_c_005fchoose_005f6.setPageContext(_jspx_page_context);
      _jspx_th_c_005fchoose_005f6.setParent(null);
      int _jspx_eval_c_005fchoose_005f6 = _jspx_th_c_005fchoose_005f6.doStartTag();
      if (_jspx_eval_c_005fchoose_005f6 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        out.write('\n');
        out.write('	');
        out.write('	');
        //  c:when
        com.liferay.taglib.core.WhenTag _jspx_th_c_005fwhen_005f7 = (com.liferay.taglib.core.WhenTag) _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.get(com.liferay.taglib.core.WhenTag.class);
        _jspx_th_c_005fwhen_005f7.setPageContext(_jspx_page_context);
        _jspx_th_c_005fwhen_005f7.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_005fchoose_005f6);
        // /html/common/themes/top_js.jspf(407,2) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
        _jspx_th_c_005fwhen_005f7.setTest( StringUtil.equalsIgnoreCase(analyticsType, "google") );
        int _jspx_eval_c_005fwhen_005f7 = _jspx_th_c_005fwhen_005f7.doStartTag();
        if (_jspx_eval_c_005fwhen_005f7 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
          out.write("\n");
          out.write("\n");
          out.write("\t\t\t");

			String googleAnalyticsId = groupTypeSettings.getProperty("googleAnalyticsId");
			
          out.write("\n");
          out.write("\n");
          out.write("\t\t\t");
          //  c:if
          com.liferay.taglib.core.IfTag _jspx_th_c_005fif_005f12 = (com.liferay.taglib.core.IfTag) _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get(com.liferay.taglib.core.IfTag.class);
          _jspx_th_c_005fif_005f12.setPageContext(_jspx_page_context);
          _jspx_th_c_005fif_005f12.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_005fwhen_005f7);
          // /html/common/themes/top_js.jspf(413,3) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
          _jspx_th_c_005fif_005f12.setTest( Validator.isNotNull(googleAnalyticsId) );
          int _jspx_eval_c_005fif_005f12 = _jspx_th_c_005fif_005f12.doStartTag();
          if (_jspx_eval_c_005fif_005f12 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            out.write("\n");
            out.write("\t\t\t\t<script type=\"text/javascript\">\n");
            out.write("\t\t\t\t\t(function(i, s, o, g, r, a, m) {\n");
            out.write("\t\t\t\t\t\ti['GoogleAnalyticsObject'] = r;\n");
            out.write("\n");
            out.write("\t\t\t\t\t\ti[r] = i[r] || function() {\n");
            out.write("\t\t\t\t\t\t\tvar arrayValue = i[r].q || [];\n");
            out.write("\n");
            out.write("\t\t\t\t\t\t\ti[r].q = arrayValue;\n");
            out.write("\n");
            out.write("\t\t\t\t\t\t\t(i[r].q).push(arguments);\n");
            out.write("\t\t\t\t\t\t};\n");
            out.write("\n");
            out.write("\t\t\t\t\t\ti[r].l = 1 * new Date();\n");
            out.write("\n");
            out.write("\t\t\t\t\t\ta = s.createElement(o);\n");
            out.write("\t\t\t\t\t\tm = s.getElementsByTagName(o)[0];\n");
            out.write("\t\t\t\t\t\ta.async = 1;\n");
            out.write("\t\t\t\t\t\ta.src = g;\n");
            out.write("\t\t\t\t\t\tm.parentNode.insertBefore(a, m);\n");
            out.write("\t\t\t\t\t})(window, document, 'script', '//www.google-analytics.com/analytics.js', 'ga');\n");
            out.write("\n");
            out.write("\t\t\t\t\tga('create', '");
            out.print( HtmlUtil.escapeJS(googleAnalyticsId) );
            out.write("', 'auto');\n");
            out.write("\t\t\t\t\tga('send', 'pageview');\n");
            out.write("\t\t\t\t</script>\n");
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
        }
        if (_jspx_th_c_005fwhen_005f7.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse(_jspx_th_c_005fwhen_005f7);
          return;
        }
        _005fjspx_005ftagPool_005fc_005fwhen_0026_005ftest.reuse(_jspx_th_c_005fwhen_005f7);
        out.write('\n');
        out.write('	');
        out.write('	');
        //  c:otherwise
        com.liferay.taglib.core.OtherwiseTag _jspx_th_c_005fotherwise_005f6 = (com.liferay.taglib.core.OtherwiseTag) _005fjspx_005ftagPool_005fc_005fotherwise.get(com.liferay.taglib.core.OtherwiseTag.class);
        _jspx_th_c_005fotherwise_005f6.setPageContext(_jspx_page_context);
        _jspx_th_c_005fotherwise_005f6.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_005fchoose_005f6);
        int _jspx_eval_c_005fotherwise_005f6 = _jspx_th_c_005fotherwise_005f6.doStartTag();
        if (_jspx_eval_c_005fotherwise_005f6 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
          out.write("\n");
          out.write("\n");
          out.write("\t\t\t");

			String analyticsScript = groupTypeSettings.getProperty(Sites.ANALYTICS_PREFIX + analyticsType);
			
          out.write("\n");
          out.write("\n");
          out.write("\t\t\t");
          //  c:if
          com.liferay.taglib.core.IfTag _jspx_th_c_005fif_005f13 = (com.liferay.taglib.core.IfTag) _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get(com.liferay.taglib.core.IfTag.class);
          _jspx_th_c_005fif_005f13.setPageContext(_jspx_page_context);
          _jspx_th_c_005fif_005f13.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_005fotherwise_005f6);
          // /html/common/themes/top_js.jspf(446,3) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
          _jspx_th_c_005fif_005f13.setTest( Validator.isNotNull(analyticsScript) );
          int _jspx_eval_c_005fif_005f13 = _jspx_th_c_005fif_005f13.doStartTag();
          if (_jspx_eval_c_005fif_005f13 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            out.write("\n");
            out.write("\t\t\t\t");
            out.print( analyticsScript );
            out.write("\n");
            out.write("\t\t\t");
          }
          if (_jspx_th_c_005fif_005f13.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
            _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f13);
            return;
          }
          _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f13);
          out.write('\n');
          out.write('	');
          out.write('	');
        }
        if (_jspx_th_c_005fotherwise_005f6.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _005fjspx_005ftagPool_005fc_005fotherwise.reuse(_jspx_th_c_005fotherwise_005f6);
          return;
        }
        _005fjspx_005ftagPool_005fc_005fotherwise.reuse(_jspx_th_c_005fotherwise_005f6);
        out.write('\n');
        out.write('	');
      }
      if (_jspx_th_c_005fchoose_005f6.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _005fjspx_005ftagPool_005fc_005fchoose.reuse(_jspx_th_c_005fchoose_005f6);
        return;
      }
      _005fjspx_005ftagPool_005fc_005fchoose.reuse(_jspx_th_c_005fchoose_005f6);
      out.write('\n');
      out.write('\n');

}

      out.write('\n');
      out.write('\n');
      out.write('\n');
      out.write('\n');
      out.write('\n');
      out.write('\n');
      out.write('\n');
      //  c:if
      com.liferay.taglib.core.IfTag _jspx_th_c_005fif_005f14 = (com.liferay.taglib.core.IfTag) _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get(com.liferay.taglib.core.IfTag.class);
      _jspx_th_c_005fif_005f14.setPageContext(_jspx_page_context);
      _jspx_th_c_005fif_005f14.setParent(null);
      // /html/common/themes/top_portlet_resources_js.jspf(17,0) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
      _jspx_th_c_005fif_005f14.setTest( (portlets != null) && !portlets.isEmpty() );
      int _jspx_eval_c_005fif_005f14 = _jspx_th_c_005fif_005f14.doStartTag();
      if (_jspx_eval_c_005fif_005f14 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        out.write('\n');
        out.write('\n');
        out.write('	');

	Set<String> portletResourceStaticURLs = (Set<String>)request.getAttribute(WebKeys.PORTLET_RESOURCE_STATIC_URLS);

	if (portletResourceStaticURLs == null) {
		portletResourceStaticURLs = new LinkedHashSet<String>();

		request.setAttribute(WebKeys.PORTLET_RESOURCE_STATIC_URLS, portletResourceStaticURLs);
	}

	for (Portlet curPortlet : portlets) {
		for (String headerPortalJavaScript : curPortlet.getHeaderPortalJavaScript()) {
			if (!HttpUtil.hasProtocol(headerPortalJavaScript)) {
				Portlet curRootPortlet = curPortlet.getRootPortlet();

				headerPortalJavaScript = PortalUtil.getStaticResourceURL(request, PortalUtil.getPathContext() + headerPortalJavaScript, curRootPortlet.getTimestamp());
			}

			if (!headerPortalJavaScript.contains(Http.PROTOCOL_DELIMITER)) {
				String cdnBaseURL = themeDisplay.getCDNBaseURL();

				headerPortalJavaScript = cdnBaseURL.concat(headerPortalJavaScript);
			}

			if (!portletResourceStaticURLs.contains(headerPortalJavaScript) && !themeDisplay.isIncludedJs(headerPortalJavaScript)) {
				portletResourceStaticURLs.add(headerPortalJavaScript);
	
        out.write("\n");
        out.write("\n");
        out.write("\t\t\t\t<script src=\"");
        out.print( HtmlUtil.escape(headerPortalJavaScript) );
        out.write("\" type=\"text/javascript\"></script>\n");
        out.write("\n");
        out.write("\t");

			}
		}
	}

	for (Portlet curPortlet : portlets) {
		for (String headerPortletJavaScript : curPortlet.getHeaderPortletJavaScript()) {
			if (!HttpUtil.hasProtocol(headerPortletJavaScript)) {
				Portlet curRootPortlet = curPortlet.getRootPortlet();

				headerPortletJavaScript = PortalUtil.getStaticResourceURL(request, curPortlet.getStaticResourcePath() + headerPortletJavaScript, curRootPortlet.getTimestamp());
			}

			if (!headerPortletJavaScript.contains(Http.PROTOCOL_DELIMITER)) {
				String cdnBaseURL = themeDisplay.getCDNBaseURL();

				headerPortletJavaScript = cdnBaseURL.concat(headerPortletJavaScript);
			}

			if (!portletResourceStaticURLs.contains(headerPortletJavaScript)) {
				portletResourceStaticURLs.add(headerPortletJavaScript);
	
        out.write("\n");
        out.write("\n");
        out.write("\t\t\t\t<script src=\"");
        out.print( HtmlUtil.escape(headerPortletJavaScript) );
        out.write("\" type=\"text/javascript\"></script>\n");
        out.write("\n");
        out.write("\t");

			}
		}
	}
	
        out.write('\n');
        out.write('\n');
      }
      if (_jspx_th_c_005fif_005f14.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f14);
        return;
      }
      _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f14);
      out.write('\n');
      out.write('\n');
      out.write('\n');
      out.write('\n');

List<String> markupHeaders = (List<String>)request.getAttribute(MimeResponse.MARKUP_HEAD_ELEMENT);

if (markupHeaders != null) {
	for (String markupHeader : markupHeaders) {

      out.write("\n");
      out.write("\n");
      out.write("\t\t");
      out.print( markupHeader );
      out.write('\n');
      out.write('\n');

	}
}

StringBundler pageTopSB = OutputTag.getData(request, WebKeys.PAGE_TOP);

      out.write('\n');
      out.write('\n');
      //  c:if
      com.liferay.taglib.core.IfTag _jspx_th_c_005fif_005f15 = (com.liferay.taglib.core.IfTag) _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get(com.liferay.taglib.core.IfTag.class);
      _jspx_th_c_005fif_005f15.setPageContext(_jspx_page_context);
      _jspx_th_c_005fif_005f15.setParent(null);
      // /html/common/themes/top_head.jsp(151,0) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
      _jspx_th_c_005fif_005f15.setTest( pageTopSB != null );
      int _jspx_eval_c_005fif_005f15 = _jspx_th_c_005fif_005f15.doStartTag();
      if (_jspx_eval_c_005fif_005f15 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        out.write('\n');
        out.write('\n');
        out.write('	');

	pageTopSB.writeTo(out);
	
        out.write('\n');
        out.write('\n');
      }
      if (_jspx_th_c_005fif_005f15.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f15);
        return;
      }
      _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f15);
      out.write('\n');
      out.write('\n');
      out.write("\n");
      out.write("\n");
      out.write("<link class=\"lfr-css-file\" href=\"");
      out.print( HtmlUtil.escapeAttribute(PortalUtil.getStaticResourceURL(request, themeDisplay.getPathThemeCss() + "/main.css")) );
      out.write("\" rel=\"stylesheet\" type=\"text/css\" />\n");
      out.write("\n");
      out.write('\n');
      out.write('\n');
      //  c:if
      com.liferay.taglib.core.IfTag _jspx_th_c_005fif_005f16 = (com.liferay.taglib.core.IfTag) _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get(com.liferay.taglib.core.IfTag.class);
      _jspx_th_c_005fif_005f16.setPageContext(_jspx_page_context);
      _jspx_th_c_005fif_005f16.setParent(null);
      // /html/common/themes/top_head.jsp(165,0) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
      _jspx_th_c_005fif_005f16.setTest( (layout != null) && Validator.isNotNull(layout.getCssText()) );
      int _jspx_eval_c_005fif_005f16 = _jspx_th_c_005fif_005f16.doStartTag();
      if (_jspx_eval_c_005fif_005f16 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        out.write("\n");
        out.write("\t<style type=\"text/css\">\n");
        out.write("\t\t");
        out.print( _escapeCssBlock(layout.getCssText()) );
        out.write("\n");
        out.write("\t</style>\n");
      }
      if (_jspx_th_c_005fif_005f16.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f16);
        return;
      }
      _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f16);
      out.write('\n');
      out.write('\n');
      out.write('\n');
      out.write('\n');
      //  c:if
      com.liferay.taglib.core.IfTag _jspx_th_c_005fif_005f17 = (com.liferay.taglib.core.IfTag) _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get(com.liferay.taglib.core.IfTag.class);
      _jspx_th_c_005fif_005f17.setPageContext(_jspx_page_context);
      _jspx_th_c_005fif_005f17.setParent(null);
      // /html/common/themes/top_head.jsp(173,0) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
      _jspx_th_c_005fif_005f17.setTest( portlets != null );
      int _jspx_eval_c_005fif_005f17 = _jspx_th_c_005fif_005f17.doStartTag();
      if (_jspx_eval_c_005fif_005f17 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        out.write("\n");
        out.write("\t<style type=\"text/css\">\n");
        out.write("\n");
        out.write("\t\t");

		for (Portlet portlet : portlets) {
			PortletPreferences portletSetup = PortletPreferencesFactoryUtil.getStrictLayoutPortletSetup(layout, portlet.getPortletId());

			String portletSetupCss = portletSetup.getValue("portletSetupCss", StringPool.BLANK);
		
        out.write("\n");
        out.write("\n");
        out.write("\t\t\t");
        //  c:if
        com.liferay.taglib.core.IfTag _jspx_th_c_005fif_005f18 = (com.liferay.taglib.core.IfTag) _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get(com.liferay.taglib.core.IfTag.class);
        _jspx_th_c_005fif_005f18.setPageContext(_jspx_page_context);
        _jspx_th_c_005fif_005f18.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_005fif_005f17);
        // /html/common/themes/top_head.jsp(183,3) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
        _jspx_th_c_005fif_005f18.setTest( Validator.isNotNull(portletSetupCss) );
        int _jspx_eval_c_005fif_005f18 = _jspx_th_c_005fif_005f18.doStartTag();
        if (_jspx_eval_c_005fif_005f18 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
          out.write("\n");
          out.write("\n");
          out.write("\t\t\t\t");

				try {
				
          out.write("\n");
          out.write("\n");
          out.write("\t\t\t\t\t");
          out.write('\n');
          out.write('\n');

JSONObject jsonObject = PortletSetupUtil.cssToJSONObject(portletSetup, portletSetupCss);

List finalCSS = new ArrayList();

// Background data

JSONObject bgData = jsonObject.getJSONObject("bgData");

String bgColor = bgData.getString("backgroundColor");
String bgImage = bgData.getString("backgroundImage");

JSONObject bgPos = bgData.getJSONObject("backgroundPosition");
JSONObject bgPosLeft = bgPos.getJSONObject(_LEFT_KEY);
JSONObject bgPosTop = bgPos.getJSONObject(_TOP_KEY);

String bgPosLeftValue = bgPosLeft.getString(_VALUE_KEY) + bgPosLeft.getString(_UNIT_KEY);
String bgPosTopValue = bgPosTop.getString(_VALUE_KEY) + bgPosTop.getString(_UNIT_KEY);
String bgPosValue = bgPosLeftValue + " " + bgPosTopValue;

boolean useBgImage = bgData.getBoolean("useBgImage");

if (Validator.isNotNull(bgColor)) {
	finalCSS.add("background-color: " + bgColor);
}

if (Validator.isNotNull(bgImage)) {
	finalCSS.add("background-image: url(" + bgImage + ")");
}

if (useBgImage) {
	finalCSS.add("background-position: " + bgPosValue);
}

// Border data

JSONObject borderData = jsonObject.getJSONObject("borderData");

JSONObject borderWidth = borderData.getJSONObject("borderWidth");
JSONObject borderStyle = borderData.getJSONObject("borderStyle");
JSONObject borderColor = borderData.getJSONObject("borderColor");

boolean ufaBorderWidth = borderWidth.getBoolean(_SAME_FOR_ALL_KEY);
boolean ufaBorderStyle = borderStyle.getBoolean(_SAME_FOR_ALL_KEY);
boolean ufaBorderColor = borderColor.getBoolean(_SAME_FOR_ALL_KEY);

// Width

JSONObject borderWidthTop = borderWidth.getJSONObject(_TOP_KEY);
JSONObject borderWidthRight = borderWidth.getJSONObject(_RIGHT_KEY);
JSONObject borderWidthBottom = borderWidth.getJSONObject(_BOTTOM_KEY);
JSONObject borderWidthLeft = borderWidth.getJSONObject(_LEFT_KEY);

String borderTopWidthValue = StringPool.BLANK;
String borderRightWidthValue = StringPool.BLANK;
String borderBottomWidthValue = StringPool.BLANK;
String borderLeftWidthValue = StringPool.BLANK;

if (Validator.isNotNull(borderWidthTop.getString(_VALUE_KEY))) {
	borderTopWidthValue = borderWidthTop.getString(_VALUE_KEY) + borderWidthTop.getString(_UNIT_KEY);
}

if (Validator.isNotNull(borderWidthRight.getString(_VALUE_KEY))) {
	borderRightWidthValue = borderWidthRight.getString(_VALUE_KEY) + borderWidthRight.getString(_UNIT_KEY);
}

if (Validator.isNotNull(borderWidthBottom.getString(_VALUE_KEY))) {
	borderBottomWidthValue = borderWidthBottom.getString(_VALUE_KEY) + borderWidthBottom.getString(_UNIT_KEY);
}

if (Validator.isNotNull(borderWidthLeft.getString(_VALUE_KEY))) {
	borderLeftWidthValue = borderWidthLeft.getString(_VALUE_KEY) + borderWidthLeft.getString(_UNIT_KEY);
}

// Style

String borderTopStyleValue = borderStyle.getString(_TOP_KEY);
String borderRightStyleValue = borderStyle.getString(_RIGHT_KEY);
String borderBottomStyleValue = borderStyle.getString(_BOTTOM_KEY);
String borderLeftStyleValue = borderStyle.getString(_LEFT_KEY);

// Color

String borderTopColorValue = borderColor.getString(_TOP_KEY);
String borderRightColorValue = borderColor.getString(_RIGHT_KEY);
String borderBottomColorValue = borderColor.getString(_BOTTOM_KEY);
String borderLeftColorValue = borderColor.getString(_LEFT_KEY);

if (ufaBorderWidth && !_unitSet.contains(borderTopWidthValue)) {
	finalCSS.add("border-width: " + borderTopWidthValue);
}
else {
	if (!_unitSet.contains(borderTopWidthValue)) {
		finalCSS.add("border-top-width: " + borderTopWidthValue);
	}

	if (!_unitSet.contains(borderRightWidthValue)) {
		finalCSS.add("border-right-width: " + borderRightWidthValue);
	}

	if (!_unitSet.contains(borderBottomWidthValue)) {
		finalCSS.add("border-bottom-width: " + borderBottomWidthValue);
	}

	if (!_unitSet.contains(borderLeftWidthValue)) {
		finalCSS.add("border-left-width: " + borderLeftWidthValue);
	}
}

if (ufaBorderStyle && !_unitSet.contains(borderTopWidthValue)) {
	finalCSS.add("border-style: " + borderTopStyleValue);
}
else {
	if (Validator.isNotNull(borderTopStyleValue)) {
		finalCSS.add("border-top-style: " + borderTopStyleValue);
	}

	if (Validator.isNotNull(borderRightStyleValue)) {
		finalCSS.add("border-right-style: " + borderRightStyleValue);
	}

	if (Validator.isNotNull(borderBottomStyleValue)) {
		finalCSS.add("border-bottom-style: " + borderBottomStyleValue);
	}

	if (Validator.isNotNull(borderLeftStyleValue)) {
		finalCSS.add("border-left-style: " + borderLeftStyleValue);
	}
}

if (ufaBorderColor) {
	if (Validator.isNotNull(borderTopColorValue)) {
		finalCSS.add("border-color: " + borderTopColorValue);
	}
}
else {
	if (Validator.isNotNull(borderTopColorValue)) {
		finalCSS.add("border-top-color: " + borderTopColorValue);
	}

	if (Validator.isNotNull(borderRightColorValue)) {
		finalCSS.add("border-right-color: " + borderRightColorValue);
	}

	if (Validator.isNotNull(borderBottomColorValue)) {
		finalCSS.add("border-bottom-color: " + borderBottomColorValue);
	}

	if (Validator.isNotNull(borderLeftColorValue)) {
		finalCSS.add("border-left-color: " + borderLeftColorValue);
	}
}

// Spacing data

JSONObject spacingData = jsonObject.getJSONObject("spacingData");

JSONObject margin = spacingData.getJSONObject("margin");
JSONObject padding = spacingData.getJSONObject("padding");

boolean ufaMargin = margin.getBoolean(_SAME_FOR_ALL_KEY);
boolean ufaPadding = padding.getBoolean(_SAME_FOR_ALL_KEY);

// Margin

JSONObject marginTop = margin.getJSONObject(_TOP_KEY);
JSONObject marginRight = margin.getJSONObject(_RIGHT_KEY);
JSONObject marginBottom = margin.getJSONObject(_BOTTOM_KEY);
JSONObject marginLeft = margin.getJSONObject(_LEFT_KEY);

String marginTopValue = marginTop.getString(_VALUE_KEY) + marginTop.getString(_UNIT_KEY);
String marginRightValue = marginRight.getString(_VALUE_KEY) + marginRight.getString(_UNIT_KEY);
String marginBottomValue = marginBottom.getString(_VALUE_KEY) + marginBottom.getString(_UNIT_KEY);
String marginLeftValue = marginLeft.getString(_VALUE_KEY) + marginLeft.getString(_UNIT_KEY);

if (ufaMargin && !_unitSet.contains(marginTopValue)) {
	finalCSS.add("margin: " + marginTopValue);
}
else {
	if (!_unitSet.contains(marginTopValue)) {
		finalCSS.add("margin-top: " + marginTopValue);
	}

	if (!_unitSet.contains(marginRightValue)) {
		finalCSS.add("margin-right: " + marginRightValue);
	}

	if (!_unitSet.contains(marginBottomValue)) {
		finalCSS.add("margin-bottom: " + marginBottomValue);
	}

	if (!_unitSet.contains(marginLeftValue)) {
		finalCSS.add("margin-left: " + marginLeftValue);
	}
}

// Padding

JSONObject paddingTop = padding.getJSONObject(_TOP_KEY);
JSONObject paddingRight = padding.getJSONObject(_RIGHT_KEY);
JSONObject paddingBottom = padding.getJSONObject(_BOTTOM_KEY);
JSONObject paddingLeft = padding.getJSONObject(_LEFT_KEY);

String paddingTopValue = paddingTop.getString(_VALUE_KEY) + paddingTop.getString(_UNIT_KEY);
String paddingRightValue = paddingRight.getString(_VALUE_KEY) + paddingRight.getString(_UNIT_KEY);
String paddingBottomValue = paddingBottom.getString(_VALUE_KEY) + paddingBottom.getString(_UNIT_KEY);
String paddingLeftValue = paddingLeft.getString(_VALUE_KEY) + paddingLeft.getString(_UNIT_KEY);

if (ufaPadding && !_unitSet.contains(paddingTopValue)) {
	finalCSS.add("padding: " + paddingTopValue);
}
else {
	if (!_unitSet.contains(paddingTopValue)) {
		finalCSS.add("padding-top: " + paddingTopValue);
	}

	if (!_unitSet.contains(paddingRightValue)) {
		finalCSS.add("padding-right: " + paddingRightValue);
	}

	if (!_unitSet.contains(paddingBottomValue)) {
		finalCSS.add("padding-bottom: " + paddingBottomValue);
	}

	if (!_unitSet.contains(paddingLeftValue)) {
		finalCSS.add("padding-left: " + paddingLeftValue);
	}
}

// Text data

JSONObject textData = jsonObject.getJSONObject("textData");

String color = textData.getString("color");
String fontFamily = textData.getString("fontFamily");
String fontSize = textData.getString("fontSize");
String fontStyle = textData.getString("fontStyle");
String fontWeight = textData.getString("fontWeight");
String letterSpacing = textData.getString("letterSpacing");
String lineHeight = textData.getString("lineHeight");
String textAlign = textData.getString("textAlign");
String textDecoration = textData.getString("textDecoration");
String wordSpacing = textData.getString("wordSpacing");

if (Validator.isNotNull(color)) {
	finalCSS.add("color: " + color);
}

if (Validator.isNotNull(fontFamily)) {
	finalCSS.add("font-family: '" + fontFamily + "'");
}

if (Validator.isNotNull(fontSize)) {
	finalCSS.add("font-size: " + fontSize);
}

if (Validator.isNotNull(fontStyle)) {
	finalCSS.add("font-style: " + fontStyle);
}

if (Validator.isNotNull(fontWeight)) {
	finalCSS.add("font-weight: " + fontWeight);
}

if (Validator.isNotNull(letterSpacing)) {
	finalCSS.add("letter-spacing: " + letterSpacing);
}

if (Validator.isNotNull(lineHeight)) {
	finalCSS.add("line-height: " + lineHeight);
}

if (Validator.isNotNull(textAlign)) {
	finalCSS.add("text-align: " + textAlign);
}

if (Validator.isNotNull(textDecoration)) {
	finalCSS.add("text-decoration: " + textDecoration);
}

if (Validator.isNotNull(wordSpacing)) {
	finalCSS.add("word-spacing: " + wordSpacing);
}

// Advanced styling

JSONObject advancedData = jsonObject.getJSONObject("advancedData");

String customCSS = advancedData.getString("customCSS");

// Portlet data

JSONObject portletData = jsonObject.getJSONObject("portletData");

boolean portletDecorate = GetterUtil.getBoolean(themeDisplay.getThemeSetting("portlet-setup-show-borders-default"), PropsValues.THEME_PORTLET_DECORATE_DEFAULT);

boolean showBorders = portletData.getBoolean("showBorders", portletDecorate);

// Generated CSS

out.print("#p_p_id_" + portlet.getPortletId() + "_");

if (showBorders) {
	out.print(" .portlet");
}
else {
	out.print(" .portlet-borderless-container");
}

out.print(" {\n");

String[] finalCSSArray = (String[])finalCSS.toArray(new String[0]);

String finalCSSString = StringUtil.merge(finalCSSArray, ";\n");

out.print(_escapeCssBlock(finalCSSString));

out.print("\n}\n");

// Advanced CSS

if (Validator.isNotNull(customCSS)) {
	out.print(_escapeCssBlock(customCSS));
}

          out.write('\n');
          out.write('\n');
          out.write("\n");
          out.write("\n");
          out.write("\t\t\t\t");

				}
				catch (Exception e) {
					if (_log.isWarnEnabled()) {
						_log.warn(e.getMessage());
					}
				}
				
          out.write("\n");
          out.write("\n");
          out.write("\t\t\t");
        }
        if (_jspx_th_c_005fif_005f18.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f18);
          return;
        }
        _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f18);
        out.write("\n");
        out.write("\n");
        out.write("\t\t");

		}
		
        out.write("\n");
        out.write("\n");
        out.write("\t</style>\n");
      }
      if (_jspx_th_c_005fif_005f17.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f17);
        return;
      }
      _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f17);
      out.write('\n');
      out.write('\n');
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

  private boolean _jspx_meth_liferay_002dtheme_005fmeta_002dtags_005f0(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-theme:meta-tags
    com.liferay.taglib.theme.MetaTagsTag _jspx_th_liferay_002dtheme_005fmeta_002dtags_005f0 = (com.liferay.taglib.theme.MetaTagsTag) _005fjspx_005ftagPool_005fliferay_002dtheme_005fmeta_002dtags_005fnobody.get(com.liferay.taglib.theme.MetaTagsTag.class);
    _jspx_th_liferay_002dtheme_005fmeta_002dtags_005f0.setPageContext(_jspx_page_context);
    _jspx_th_liferay_002dtheme_005fmeta_002dtags_005f0.setParent(null);
    int _jspx_eval_liferay_002dtheme_005fmeta_002dtags_005f0 = _jspx_th_liferay_002dtheme_005fmeta_002dtags_005f0.doStartTag();
    if (_jspx_th_liferay_002dtheme_005fmeta_002dtags_005f0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _005fjspx_005ftagPool_005fliferay_002dtheme_005fmeta_002dtags_005fnobody.reuse(_jspx_th_liferay_002dtheme_005fmeta_002dtags_005f0);
      return true;
    }
    _005fjspx_005ftagPool_005fliferay_002dtheme_005fmeta_002dtags_005fnobody.reuse(_jspx_th_liferay_002dtheme_005fmeta_002dtags_005f0);
    return false;
  }

  private boolean _jspx_meth_liferay_002dui_005fmessage_005f0(javax.servlet.jsp.tagext.JspTag _jspx_th_c_005fif_005f6, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  liferay-ui:message
    com.liferay.taglib.ui.MessageTag _jspx_th_liferay_002dui_005fmessage_005f0 = (com.liferay.taglib.ui.MessageTag) _005fjspx_005ftagPool_005fliferay_002dui_005fmessage_0026_005fkey_005fnobody.get(com.liferay.taglib.ui.MessageTag.class);
    _jspx_th_liferay_002dui_005fmessage_005f0.setPageContext(_jspx_page_context);
    _jspx_th_liferay_002dui_005fmessage_005f0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_005fif_005f6);
    // /html/common/themes/top_js.jspf(104,94) name = key type = null reqTime = true required = true fragment = false deferredValue = false deferredMethod = false expectedTypeName = null methodSignature = null 
    _jspx_th_liferay_002dui_005fmessage_005f0.setKey("updates-are-available-for-liferay");
    int _jspx_eval_liferay_002dui_005fmessage_005f0 = _jspx_th_liferay_002dui_005fmessage_005f0.doStartTag();
    if (_jspx_th_liferay_002dui_005fmessage_005f0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _005fjspx_005ftagPool_005fliferay_002dui_005fmessage_0026_005fkey_005fnobody.reuse(_jspx_th_liferay_002dui_005fmessage_005f0);
      return true;
    }
    _005fjspx_005ftagPool_005fliferay_002dui_005fmessage_0026_005fkey_005fnobody.reuse(_jspx_th_liferay_002dui_005fmessage_005f0);
    return false;
  }

  private boolean _jspx_meth_c_005fotherwise_005f0(javax.servlet.jsp.tagext.JspTag _jspx_th_c_005fchoose_005f0, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  c:otherwise
    com.liferay.taglib.core.OtherwiseTag _jspx_th_c_005fotherwise_005f0 = (com.liferay.taglib.core.OtherwiseTag) _005fjspx_005ftagPool_005fc_005fotherwise.get(com.liferay.taglib.core.OtherwiseTag.class);
    _jspx_th_c_005fotherwise_005f0.setPageContext(_jspx_page_context);
    _jspx_th_c_005fotherwise_005f0.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_005fchoose_005f0);
    int _jspx_eval_c_005fotherwise_005f0 = _jspx_th_c_005fotherwise_005f0.doStartTag();
    if (_jspx_eval_c_005fotherwise_005f0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
      out.write("\n");
      out.write("\t\t\t\t\t\t\treturn \"\";\n");
      out.write("\t\t\t\t\t\t");
    }
    if (_jspx_th_c_005fotherwise_005f0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _005fjspx_005ftagPool_005fc_005fotherwise.reuse(_jspx_th_c_005fotherwise_005f0);
      return true;
    }
    _005fjspx_005ftagPool_005fc_005fotherwise.reuse(_jspx_th_c_005fotherwise_005f0);
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
      out.write("\t\t\t\t\t\t\treturn \"\";\n");
      out.write("\t\t\t\t\t\t");
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
      out.write("\t\t\t\t\t\treturn 'raw';\n");
      out.write("\t\t\t\t\t");
    }
    if (_jspx_th_c_005fotherwise_005f2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _005fjspx_005ftagPool_005fc_005fotherwise.reuse(_jspx_th_c_005fotherwise_005f2);
      return true;
    }
    _005fjspx_005ftagPool_005fc_005fotherwise.reuse(_jspx_th_c_005fotherwise_005f2);
    return false;
  }
}
