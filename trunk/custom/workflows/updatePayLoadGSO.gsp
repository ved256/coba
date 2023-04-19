<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<goldensource-package version="8.7.1.28">
<package-comment/>
<businessobject displayString="4 - notnull" type="com.j2fe.workflow.definition.Workflow">
<com.j2fe.workflow.definition.Workflow id="0">
<alwaysPersist>false</alwaysPersist>
<clustered>true</clustered>
<comment id="1">notnull</comment>
<endNode id="2">
<activation>INVOKE</activation>
<clusteredCall>false</clusteredCall>
<directJoin>false</directJoin>
<name id="3">Stop</name>
<nodeHandler>com.j2fe.workflow.handler.impl.DummyActivityHandler</nodeHandler>
<nodeHandlerClass id="4">com.j2fe.workflow.handler.impl.DummyActivityHandler</nodeHandlerClass>
<sources id="5" type="java.util.HashSet">
<item id="6" type="com.j2fe.workflow.definition.Transition">
<name id="7">goto-next</name>
<source id="8">
<activation>INVOKE</activation>
<clusteredCall>false</clusteredCall>
<directJoin>false</directJoin>
<name id="9">Bean Shell Script (Standard) #3</name>
<nodeHandler>com.j2fe.general.activities.BeanShellScript</nodeHandler>
<nodeHandlerClass id="10">com.j2fe.general.activities.BeanShellScript</nodeHandlerClass>
<parameters id="11" type="java.util.HashSet">
<item id="12" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="13">statements</name>
<stringValue id="14">
if ( "".equals(outputMessage))
outputMessage="";
else
PayLoad=outputMessage;
</stringValue>
<type>CONSTANT</type>
</item>
<item id="15" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="16">variables["PayLoad"]</name>
<stringValue id="17">PayLoad</stringValue>
<type>VARIABLE</type>
</item>
<item id="18" type="com.j2fe.workflow.definition.Parameter">
<input>false</input>
<name id="19">variables["PayLoad"]</name>
<stringValue id="20">PayLoad</stringValue>
<type>VARIABLE</type>
</item>
<item id="21" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="22">variables["outputMessage"]</name>
<stringValue id="23">outputMessage</stringValue>
<type>VARIABLE</type>
</item>
</parameters>
<sources id="24" type="java.util.HashSet">
<item id="25" type="com.j2fe.workflow.definition.Transition">
<name id="26">goto-next</name>
<source id="27">
<activation>INVOKE</activation>
<clusteredCall>false</clusteredCall>
<directJoin>false</directJoin>
<name id="28">Bean Shell Script (Standard) #2</name>
<nodeHandler>com.j2fe.general.activities.BeanShellScript</nodeHandler>
<nodeHandlerClass id="29">com.j2fe.general.activities.BeanShellScript</nodeHandlerClass>
<parameters id="30" type="java.util.HashSet">
<item id="31" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="32">statements</name>
<objectValue id="33" type="java.lang.String">import java.io.StringReader;&#13;
import java.io.StringWriter;&#13;
&#13;
import javax.xml.parsers.DocumentBuilder;&#13;
import javax.xml.parsers.DocumentBuilderFactory;&#13;
import javax.xml.transform.Transformer;&#13;
import javax.xml.transform.TransformerFactory;&#13;
import javax.xml.transform.dom.DOMSource;&#13;
import javax.xml.transform.stream.StreamResult;&#13;
&#13;
import org.w3c.dom.Document;&#13;
import org.w3c.dom.Element;&#13;
import org.w3c.dom.Node;&#13;
import org.w3c.dom.NodeList;&#13;
import org.xml.sax.InputSource;&#13;
&#13;
String xml=new String();&#13;
String outputXml = new String();&#13;
&#13;
		try{&#13;
        &#13;
		xml=PayLoad;&#13;
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();&#13;
		InputSource src = new InputSource();&#13;
		src.setCharacterStream(new StringReader(xml));&#13;
&#13;
		Document doc = builder.parse(src);&#13;
		&#13;
    Element elm = (Element) doc.getElementsByTagName(gsoName).item(0);&#13;
		NodeList  childList = elm.getChildNodes();&#13;
		int childNums=0;&#13;
		&#13;
		boolean foundFlag = false;&#13;
		&#13;
		Node child = null;&#13;
		childNums =  childList.getLength();&#13;
		&#13;
		for ( int i=0; i&lt;childNums;i++)&#13;
		   {&#13;
			child = childList.item(i);&#13;
			if ( parentNodeName.equals(child.getNodeName()))&#13;
			     {&#13;
					foundFlag=true;&#13;
					break;&#13;
			     }&#13;
		   }&#13;
		&#13;
		if ( !(nodeName.equalsIgnoreCase(""))    &amp;&amp; foundFlag &amp;&amp; !( parentNodeName.equalsIgnoreCase(nodeName) ) )&#13;
		{&#13;
		childList = null;&#13;
		childList = child.getChildNodes();&#13;
		foundFlag=false;&#13;
		&#13;
		    child = null;&#13;
		    childNums = childList.getLength();&#13;
		&#13;
		    for ( int i=0; i &lt; childNums ; i++)&#13;
		    {	&#13;
		    	child= childList.item(i);&#13;
			    if ( nodeName.equals(child.getNodeName()))&#13;
					{&#13;
					 foundFlag=true;&#13;
				     break;&#13;
					}&#13;
			&#13;
		    }&#13;
		}&#13;
		&#13;
		if ( (child != null) &amp;&amp; (foundFlag))&#13;
		{&#13;
				child.setTextContent(nodeNewVal);&#13;
				((Element) child).setAttribute("modified", "true");&#13;
		}&#13;
	&#13;
	&#13;
		DOMSource domSource = new DOMSource(doc);&#13;
		StringWriter writer = new StringWriter();&#13;
		StreamResult result = new StreamResult(writer);&#13;
		TransformerFactory tf = TransformerFactory.newInstance();&#13;
		Transformer transformer = tf.newTransformer();&#13;
		transformer.transform(domSource, result);&#13;
&#13;
     	outputXml=writer.toString();&#13;
		&#13;
		}&#13;
		&#13;
		catch(Exception e){&#13;
		System.out.println(e);	&#13;
		}&#13;
	</objectValue>
<type>CONSTANT</type>
</item>
<item id="34" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="35">variables["PayLoad"]</name>
<stringValue id="36">PayLoad</stringValue>
<type>VARIABLE</type>
</item>
<item id="37" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="38">variables["gsoName"]</name>
<stringValue id="39">rootNodeName</stringValue>
<type>VARIABLE</type>
</item>
<item id="40" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="41">variables["nodeName"]</name>
<stringValue id="42">nodeName</stringValue>
<type>VARIABLE</type>
</item>
<item id="43" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="44">variables["nodeNewVal"]</name>
<stringValue id="45">nodeNewVal</stringValue>
<type>VARIABLE</type>
</item>
<item id="46" type="com.j2fe.workflow.definition.Parameter">
<input>false</input>
<name id="47">variables["outputXml"]</name>
<stringValue id="48">outputMessage</stringValue>
<type>VARIABLE</type>
</item>
<item id="49" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="50">variables["parentNodeName"]</name>
<stringValue id="51">parentNodeName</stringValue>
<type>VARIABLE</type>
</item>
</parameters>
<sources id="52" type="java.util.HashSet">
<item id="53" type="com.j2fe.workflow.definition.Transition">
<name id="54">goto-next</name>
<source id="55">
<activation>INVOKE</activation>
<clusteredCall>false</clusteredCall>
<directJoin>false</directJoin>
<name id="56">Start</name>
<nodeHandler>com.j2fe.workflow.handler.impl.DummyActivityHandler</nodeHandler>
<nodeHandlerClass id="57">com.j2fe.workflow.handler.impl.DummyActivityHandler</nodeHandlerClass>
<sources id="58" type="java.util.HashSet"/>
<targets id="59" type="java.util.HashSet">
<item idref="53" type="com.j2fe.workflow.definition.Transition"/>
</targets>
<type>START</type>
</source>
<target idref="27"/>
</item>
</sources>
<targets id="60" type="java.util.HashSet">
<item idref="25" type="com.j2fe.workflow.definition.Transition"/>
</targets>
<type>ACTIVITY</type>
</source>
<target idref="8"/>
</item>
</sources>
<targets id="61" type="java.util.HashSet">
<item idref="6" type="com.j2fe.workflow.definition.Transition"/>
</targets>
<type>ACTIVITY</type>
</source>
<target idref="2"/>
</item>
</sources>
<targets id="62" type="java.util.HashSet"/>
<type>END</type>
</endNode>
<forcePurgeAtEnd>false</forcePurgeAtEnd>
<group id="63">RMB/Common</group>
<haltOnError>false</haltOnError>
<lastChangeUser id="64">user1</lastChangeUser>
<lastUpdate id="65">2018-10-16T15:20:06.000+0530</lastUpdate>
<name id="66">updatePayLoadGSO</name>
<optimize>true</optimize>
<parameter id="67" type="java.util.HashMap">
<entry>
<key id="68" type="java.lang.String">PayLoad</key>
<value id="69" type="com.j2fe.workflow.definition.WorkflowParameter">
<className id="70">java.lang.String</className>
<clazz>java.lang.String</clazz>
<input>true</input>
<output>true</output>
<required>true</required>
</value>
</entry>
<entry>
<key id="71" type="java.lang.String">TaskID</key>
<value id="72" type="com.j2fe.workflow.definition.WorkflowParameter">
<className id="73">java.lang.String</className>
<clazz>java.lang.String</clazz>
<input>true</input>
<output>false</output>
<required>true</required>
</value>
</entry>
<entry>
<key id="74" type="java.lang.String">nodeName</key>
<value id="75" type="com.j2fe.workflow.definition.WorkflowParameter">
<className id="76">java.lang.String</className>
<clazz>java.lang.String</clazz>
<input>true</input>
<output>false</output>
<required>true</required>
</value>
</entry>
<entry>
<key id="77" type="java.lang.String">nodeNewVal</key>
<value id="78" type="com.j2fe.workflow.definition.WorkflowParameter">
<className id="79">java.lang.String</className>
<clazz>java.lang.String</clazz>
<input>true</input>
<output>false</output>
<required>false</required>
</value>
</entry>
<entry>
<key id="80" type="java.lang.String">parentNodeName</key>
<value id="81" type="com.j2fe.workflow.definition.WorkflowParameter">
<className id="82">java.lang.String</className>
<clazz>java.lang.String</clazz>
<input>true</input>
<output>false</output>
<required>true</required>
</value>
</entry>
<entry>
<key id="83" type="java.lang.String">rootNodeName</key>
<value id="84" type="com.j2fe.workflow.definition.WorkflowParameter">
<className id="85">java.lang.String</className>
<clazz>java.lang.String</clazz>
<input>true</input>
<output>false</output>
<required>true</required>
</value>
</entry>
</parameter>
<permissions id="86" type="java.util.HashSet"/>
<priority>50</priority>
<purgeAtEnd>true</purgeAtEnd>
<retries>0</retries>
<startNode idref="55"/>
<status>RELEASED</status>
<variables id="87" type="java.util.HashMap">
<entry>
<key id="88" type="java.lang.String">PayLoad</key>
<value id="89" type="com.j2fe.workflow.definition.GlobalVariable">
<className id="90">java.lang.String</className>
<clazz>java.lang.String</clazz>
<persistent>true</persistent>
</value>
</entry>
<entry>
<key id="91" type="java.lang.String">TaskID</key>
<value id="92" type="com.j2fe.workflow.definition.GlobalVariable">
<className id="93">java.lang.String</className>
<clazz>java.lang.String</clazz>
<persistent>false</persistent>
</value>
</entry>
<entry>
<key id="94" type="java.lang.String">nodeName</key>
<value id="95" type="com.j2fe.workflow.definition.GlobalVariable">
<className id="96">java.lang.String</className>
<clazz>java.lang.String</clazz>
<persistent>false</persistent>
</value>
</entry>
<entry>
<key id="97" type="java.lang.String">nodeNewVal</key>
<value id="98" type="com.j2fe.workflow.definition.GlobalVariable">
<className id="99">java.lang.String</className>
<clazz>java.lang.String</clazz>
<persistent>false</persistent>
</value>
</entry>
<entry>
<key id="100" type="java.lang.String">parentNodeName</key>
<value id="101" type="com.j2fe.workflow.definition.GlobalVariable">
<className id="102">java.lang.String</className>
<clazz>java.lang.String</clazz>
<persistent>false</persistent>
</value>
</entry>
<entry>
<key id="103" type="java.lang.String">rootNodeName</key>
<value id="104" type="com.j2fe.workflow.definition.GlobalVariable">
<className id="105">java.lang.String</className>
<clazz>java.lang.String</clazz>
<persistent>false</persistent>
</value>
</entry>
</variables>
<version>4</version>
</com.j2fe.workflow.definition.Workflow>
</businessobject>
</goldensource-package>
