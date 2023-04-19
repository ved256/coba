<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<goldensource-package version="8.7.1.58">
<package-comment>Workflows:&#13;
	+ RMBCustom RMBPVDDBCompare  (6 - 8.7.1.29.3-No_Cluster)&#13;
		Optimized, Purge at end, Retries: 0&#13;
</package-comment>
<businessobject displayString="6 - 8.7.1.29.3-No_Cluster" type="com.j2fe.workflow.definition.Workflow">
<com.j2fe.workflow.definition.Workflow id="0">
<alwaysPersist>false</alwaysPersist>
<clustered>false</clustered>
<comment id="1">8.7.1.29.3-No_Cluster</comment>
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
<name id="9">Database Statement (Standard)</name>
<nodeHandler>com.j2fe.general.activities.database.DBStatement</nodeHandler>
<nodeHandlerClass id="10">com.j2fe.general.activities.database.DBStatement</nodeHandlerClass>
<parameters id="11" type="java.util.HashSet">
<item id="12" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="13">database</name>
<stringValue id="14">DataSource</stringValue>
<type>VARIABLE</type>
</item>
<item id="15" type="com.j2fe.workflow.definition.Parameter">
<UITypeHint id="16">[0]@java/lang/String@</UITypeHint>
<input>true</input>
<name id="17">indexedParameters[0]</name>
<stringValue id="18">evProcessingIdentifier</stringValue>
<type>VARIABLE</type>
</item>
<item id="19" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="20">querySQL</name>
<stringValue id="21">UPDATE FT_T_BEVI SET EXEC_STAT_TYP = 'COMPLETE', END_TMS = SYSDATE where BEVI_OID = ?</stringValue>
<type>CONSTANT</type>
</item>
</parameters>
<sources id="22" type="java.util.HashSet">
<item id="23" type="com.j2fe.workflow.definition.Transition">
<name id="24">found Running Instances</name>
<source id="25">
<activation>INVOKE</activation>
<clusteredCall>false</clusteredCall>
<directJoin>false</directJoin>
<name id="26">Check Running Instances</name>
<nodeHandler>com.thegoldensource.batchrules.activity.EVCreateProcessingInstance</nodeHandler>
<nodeHandlerClass id="27">com.thegoldensource.batchrules.activity.EVCreateProcessingInstance</nodeHandlerClass>
<parameters id="28" type="java.util.HashSet">
<item id="29" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="30">candidateFilter</name>
<stringValue id="31">Candidate Filter</stringValue>
<type>VARIABLE</type>
</item>
<item id="32" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="33">dataSource</name>
<stringValue id="34">DataSource</stringValue>
<type>VARIABLE</type>
</item>
<item id="35" type="com.j2fe.workflow.definition.Parameter">
<input>false</input>
<name id="36">evProcessingIdentifier</name>
<stringValue id="37">evProcessingIdentifier</stringValue>
<type>VARIABLE</type>
</item>
<item id="38" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="39">gsoName</name>
<stringValue id="40">GSO Name</stringValue>
<type>VARIABLE</type>
</item>
<item id="41" type="com.j2fe.workflow.definition.Parameter">
<input>false</input>
<name id="42">lastChangeUser</name>
<stringValue id="43">lastChangeUser</stringValue>
<type>VARIABLE</type>
</item>
<item id="44" type="com.j2fe.workflow.definition.Parameter">
<input>false</input>
<name id="45">lastProcessedDate</name>
<stringValue id="46">lastProcessedDate</stringValue>
<type>VARIABLE</type>
</item>
<item id="47" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="48">processName</name>
<stringValue id="49">VDDB_COMPARE</stringValue>
<type>CONSTANT</type>
</item>
</parameters>
<persistentVariables id="50" type="java.util.HashSet">
<item id="51" type="java.lang.String">evProcessingIdentifier
1000</item>
<item id="52" type="java.lang.String">lastChangeUser
1000</item>
</persistentVariables>
<sources id="53" type="java.util.HashSet">
<item id="54" type="com.j2fe.workflow.definition.Transition">
<name id="55">false</name>
<source id="56">
<activation>INVOKE</activation>
<clusteredCall>false</clusteredCall>
<directJoin>false</directJoin>
<name id="57">Switch Case</name>
<nodeHandler>com.j2fe.workflow.handler.impl.SwitchCaseSplit</nodeHandler>
<nodeHandlerClass id="58">com.j2fe.workflow.handler.impl.SwitchCaseSplit</nodeHandlerClass>
<parameters id="59" type="java.util.HashSet">
<item id="60" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="61">caseItem</name>
<stringValue id="62">inputError</stringValue>
<type>VARIABLE</type>
</item>
<item id="63" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="64">defaultItem</name>
<stringValue id="65">inputError</stringValue>
<type>VARIABLE</type>
</item>
<item id="66" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="67">nullTransition</name>
<stringValue id="68">inputError</stringValue>
<type>VARIABLE</type>
</item>
</parameters>
<sources id="69" type="java.util.HashSet">
<item id="70" type="com.j2fe.workflow.definition.Transition">
<name id="71">goto-next</name>
<source id="72">
<activation>INVOKE</activation>
<clusteredCall>false</clusteredCall>
<directJoin>false</directJoin>
<name id="73">Validate Input</name>
<nodeHandler>com.j2fe.general.activities.BeanShellScript</nodeHandler>
<nodeHandlerClass id="74">com.j2fe.general.activities.BeanShellScript</nodeHandlerClass>
<parameters id="75" type="java.util.HashSet">
<item id="76" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="77">statements</name>
<stringValue id="78">boolean inputError =false;

String errorMessage = "Please enter both the filter condition and table type...";

if ((candidateFilter == null &amp;&amp; tableType != null) || (candidateFilter != null &amp;&amp; tableType == null))

{

    inputError = true;

}

else

if (GSOName != null &amp;&amp; (candidateFilter == null || tableType == null))

{

	inputError = true;

       errorMessage = "Please enter Table Type and Candidate Filter...";

}



</stringValue>
<type>CONSTANT</type>
</item>
<item id="79" type="com.j2fe.workflow.definition.Parameter">
<UITypeHint id="80">["GSOName"]@java/lang/String@</UITypeHint>
<input>true</input>
<name id="81">variables["GSOName"]</name>
<stringValue id="82">GSO Name</stringValue>
<type>VARIABLE</type>
</item>
<item id="83" type="com.j2fe.workflow.definition.Parameter">
<UITypeHint id="84">["candidateFilter"]@java/lang/String@</UITypeHint>
<input>true</input>
<name id="85">variables["candidateFilter"]</name>
<stringValue id="86">Candidate Filter</stringValue>
<type>VARIABLE</type>
</item>
<item id="87" type="com.j2fe.workflow.definition.Parameter">
<UITypeHint id="88">["errorMessage"]@java/lang/String@</UITypeHint>
<input>false</input>
<name id="89">variables["errorMessage"]</name>
<stringValue id="90">errorMessage</stringValue>
<type>VARIABLE</type>
</item>
<item id="91" type="com.j2fe.workflow.definition.Parameter">
<UITypeHint id="92">["inputError"]@java/lang/Boolean@</UITypeHint>
<input>false</input>
<name id="93">variables["inputError"]</name>
<stringValue id="94">inputError</stringValue>
<type>VARIABLE</type>
</item>
<item id="95" type="com.j2fe.workflow.definition.Parameter">
<UITypeHint id="96">["tableType"]@java/lang/String@</UITypeHint>
<input>true</input>
<name id="97">variables["tableType"]</name>
<stringValue id="98">Table Type</stringValue>
<type>VARIABLE</type>
</item>
</parameters>
<sources id="99" type="java.util.HashSet">
<item id="100" type="com.j2fe.workflow.definition.Transition">
<name id="101">goto-next</name>
<source id="102">
<activation>INVOKE</activation>
<clusteredCall>false</clusteredCall>
<directJoin>false</directJoin>
<name id="103">Start</name>
<nodeHandler>com.j2fe.workflow.handler.impl.DummyActivityHandler</nodeHandler>
<nodeHandlerClass id="104">com.j2fe.workflow.handler.impl.DummyActivityHandler</nodeHandlerClass>
<sources id="105" type="java.util.HashSet"/>
<targets id="106" type="java.util.HashSet">
<item idref="100" type="com.j2fe.workflow.definition.Transition"/>
</targets>
<type>START</type>
</source>
<target idref="72"/>
</item>
</sources>
<targets id="107" type="java.util.HashSet">
<item idref="70" type="com.j2fe.workflow.definition.Transition"/>
</targets>
<type>ACTIVITY</type>
</source>
<target idref="56"/>
</item>
</sources>
<targets id="108" type="java.util.HashSet">
<item idref="54" type="com.j2fe.workflow.definition.Transition"/>
<item id="109" type="com.j2fe.workflow.definition.Transition">
<name id="110">true</name>
<source idref="56"/>
<target id="111">
<activation>INVOKE</activation>
<clusteredCall>false</clusteredCall>
<directJoin>false</directJoin>
<name id="112">Throw Error</name>
<nodeHandler>com.j2fe.workflow.handler.impl.ThrowError</nodeHandler>
<nodeHandlerClass id="113">com.j2fe.workflow.handler.impl.ThrowError</nodeHandlerClass>
<parameters id="114" type="java.util.HashSet">
<item id="115" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="116">message</name>
<stringValue id="117">errorMessage</stringValue>
<type>VARIABLE</type>
</item>
</parameters>
<sources id="118" type="java.util.HashSet">
<item idref="109" type="com.j2fe.workflow.definition.Transition"/>
</sources>
<targets id="119" type="java.util.HashSet">
<item id="120" type="com.j2fe.workflow.definition.Transition">
<name id="121">goto-next</name>
<source idref="111"/>
<target idref="8"/>
</item>
</targets>
<type>ACTIVITY</type>
</target>
</item>
</targets>
<type>XORSPLIT</type>
</source>
<target idref="25"/>
</item>
</sources>
<targets id="122" type="java.util.HashSet">
<item idref="23" type="com.j2fe.workflow.definition.Transition"/>
<item id="123" type="com.j2fe.workflow.definition.Transition">
<name id="124">no Running Instances</name>
<source idref="25"/>
<target id="125">
<activation>INVOKE</activation>
<clusteredCall>false</clusteredCall>
<directJoin>false</directJoin>
<name id="126">EV Select And Group Entities</name>
<nodeHandler>com.thegoldensource.batchrules.activity.EVSelectAndGroupEntities</nodeHandler>
<nodeHandlerClass id="127">com.thegoldensource.batchrules.activity.EVSelectAndGroupEntities</nodeHandlerClass>
<parameters id="128" type="java.util.HashSet">
<item id="129" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="130">candidateFilter</name>
<stringValue id="131">Candidate Filter</stringValue>
<type>VARIABLE</type>
</item>
<item id="132" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="133">dataSource</name>
<stringValue id="134">DataSource</stringValue>
<type>VARIABLE</type>
</item>
<item id="135" type="com.j2fe.workflow.definition.Parameter">
<input>false</input>
<name id="136">evProcessingDefinitions</name>
<stringValue id="137">evProcessingDefinitions</stringValue>
<type>VARIABLE</type>
</item>
<item id="138" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="139">extractorFactory</name>
<stringValue id="140">DataSource</stringValue>
<type>VARIABLE</type>
</item>
<item id="141" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="142">gsoName</name>
<stringValue id="143">GSO Name</stringValue>
<type>VARIABLE</type>
</item>
<item id="144" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="145">lastProcessedDate</name>
<stringValue id="146">lastProcessedDate</stringValue>
<type>VARIABLE</type>
</item>
<item id="147" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="148">sessionFactory</name>
<stringValue id="149">jdbc/GSDM-1</stringValue>
<type>REFERENCE</type>
</item>
<item id="150" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="151">tableType</name>
<stringValue id="152">Table Type</stringValue>
<type>VARIABLE</type>
</item>
<item id="153" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="154">templateName</name>
<stringValue id="155">UI Template Name</stringValue>
<type>VARIABLE</type>
</item>
</parameters>
<sources id="156" type="java.util.HashSet">
<item idref="123" type="com.j2fe.workflow.definition.Transition"/>
</sources>
<targets id="157" type="java.util.HashSet">
<item id="158" type="com.j2fe.workflow.definition.Transition">
<name id="159">found entities</name>
<source idref="125"/>
<target id="160">
<activation>INVOKE</activation>
<clusteredCall>false</clusteredCall>
<directJoin>false</directJoin>
<name id="161">EV Write Extraction Log</name>
<nodeHandler>com.thegoldensource.batchrules.activity.EVWriteExtractionLog</nodeHandler>
<nodeHandlerClass id="162">com.thegoldensource.batchrules.activity.EVWriteExtractionLog</nodeHandlerClass>
<parameters id="163" type="java.util.HashSet">
<item id="164" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="165">entityMode</name>
<objectValue id="166" type="com.thegoldensource.publishing.util.EntityMode">BUSINESS_ENTITY</objectValue>
<type>CONSTANT</type>
</item>
<item id="167" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="168">extractAllFieldsOfRelatedEntity</name>
<objectValue id="169" type="java.lang.Boolean">false</objectValue>
<type>CONSTANT</type>
</item>
<item id="170" type="com.j2fe.workflow.definition.Parameter">
<input>false</input>
<name id="171">extractDefinitions</name>
<stringValue id="172">extractDefinitions</stringValue>
<type>VARIABLE</type>
</item>
<item id="173" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="174">extractionTempTableName</name>
<objectValue id="175" type="com.thegoldensource.be.service.ExtractionTempTableName">FT_O_EXLG</objectValue>
<type>CONSTANT</type>
</item>
<item id="176" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="177">extractorFactory</name>
<stringValue id="178">DataSource</stringValue>
<type>VARIABLE</type>
</item>
<item id="179" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="180">maxExtractCount</name>
<stringValue id="181">BulkSize</stringValue>
<type>VARIABLE</type>
</item>
<item id="182" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="183">modelName</name>
<stringValue id="184">DUMMY</stringValue>
<type>CONSTANT</type>
</item>
<item id="185" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="186">processingDefinitions</name>
<stringValue id="187">evProcessingDefinitions</stringValue>
<type>VARIABLE</type>
</item>
</parameters>
<persistentVariables id="188" type="java.util.HashSet">
<item id="189" type="java.lang.String">evProcessingDefinitions
0100</item>
</persistentVariables>
<sources id="190" type="java.util.HashSet">
<item idref="158" type="com.j2fe.workflow.definition.Transition"/>
</sources>
<targets id="191" type="java.util.HashSet">
<item id="192" type="com.j2fe.workflow.definition.Transition">
<name id="193">extraction</name>
<source idref="160"/>
<target id="194">
<activation>INVOKE</activation>
<clusteredCall>false</clusteredCall>
<directJoin>false</directJoin>
<name id="195">Bean Shell Script (Standard)</name>
<nodeHandler>com.j2fe.general.activities.BeanShellScript</nodeHandler>
<nodeHandlerClass id="196">com.j2fe.general.activities.BeanShellScript</nodeHandlerClass>
<parameters id="197" type="java.util.HashSet">
<item id="198" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="199">statements</name>
<stringValue id="200">String processingStatus = "COMPLETE";
Integer numberOfBulks = extractionSize/threadSize;
if(numberOfBulks == 0)
	numberOfBulks = 1;</stringValue>
<type>CONSTANT</type>
</item>
<item id="201" type="com.j2fe.workflow.definition.Parameter">
<UITypeHint id="202">["extractionSize"]@java/lang/Integer@</UITypeHint>
<input>true</input>
<name id="203">variables["extractionSize"]</name>
<stringValue id="204">extractDefinitions</stringValue>
<type>VARIABLE</type>
<variablePart id="205">length</variablePart>
</item>
<item id="206" type="com.j2fe.workflow.definition.Parameter">
<UITypeHint id="207">["numberOfBulks"]@java/lang/Integer@</UITypeHint>
<input>false</input>
<name id="208">variables["numberOfBulks"]</name>
<stringValue id="209">extractionBulkSize</stringValue>
<type>VARIABLE</type>
</item>
<item id="210" type="com.j2fe.workflow.definition.Parameter">
<input>false</input>
<name id="211">variables["processingStatus"]</name>
<stringValue id="212">processingStatus</stringValue>
<type>VARIABLE</type>
</item>
<item id="213" type="com.j2fe.workflow.definition.Parameter">
<UITypeHint id="214">["threadSize"]@java/lang/Integer@</UITypeHint>
<input>true</input>
<name id="215">variables["threadSize"]</name>
<stringValue id="216">NumberOfThreads</stringValue>
<type>VARIABLE</type>
</item>
</parameters>
<sources id="217" type="java.util.HashSet">
<item idref="192" type="com.j2fe.workflow.definition.Transition"/>
</sources>
<targets id="218" type="java.util.HashSet">
<item id="219" type="com.j2fe.workflow.definition.Transition">
<name id="220">goto-next</name>
<source idref="194"/>
<target id="221">
<activation>ASYNCHRONOUS</activation>
<clusteredCall>false</clusteredCall>
<directJoin>true</directJoin>
<name id="222">Bulk Items</name>
<nodeHandler>com.j2fe.general.activities.BulkItems</nodeHandler>
<nodeHandlerClass id="223">com.j2fe.general.activities.BulkItems</nodeHandlerClass>
<parameters id="224" type="java.util.HashSet">
<item id="225" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="226">bulk</name>
<stringValue id="227">extractionBulkSize</stringValue>
<type>VARIABLE</type>
</item>
<item id="228" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="229">input</name>
<stringValue id="230">extractDefinitions</stringValue>
<type>VARIABLE</type>
</item>
<item id="231" type="com.j2fe.workflow.definition.Parameter">
<input>false</input>
<name id="232">output</name>
<stringValue id="233">BulkedItems</stringValue>
<type>VARIABLE</type>
</item>
</parameters>
<persistentVariables id="234" type="java.util.HashSet">
<item id="235" type="java.lang.String">BulkedItems
1000</item>
<item id="236" type="java.lang.String">extractDefinitions
0100</item>
</persistentVariables>
<sources id="237" type="java.util.HashSet">
<item idref="219" type="com.j2fe.workflow.definition.Transition"/>
</sources>
<targets id="238" type="java.util.HashSet">
<item id="239" type="com.j2fe.workflow.definition.Transition">
<name id="240">goto-next</name>
<source idref="221"/>
<target id="241">
<activation>INVOKE</activation>
<clusteredCall>false</clusteredCall>
<directJoin>false</directJoin>
<name id="242">Bean Shell Script (Standard) #2</name>
<nodeHandler>com.j2fe.general.activities.BeanShellScript</nodeHandler>
<nodeHandlerClass id="243">com.j2fe.general.activities.BeanShellScript</nodeHandlerClass>
<parameters id="244" type="java.util.HashSet">
<item id="245" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="246">statements</name>
<stringValue id="247">Integer loopCounter = 0;</stringValue>
<type>CONSTANT</type>
</item>
<item id="248" type="com.j2fe.workflow.definition.Parameter">
<UITypeHint id="249">["loopCounter"]@java/lang/Integer@</UITypeHint>
<input>false</input>
<name id="250">variables["loopCounter"]</name>
<stringValue id="251">loopCounter</stringValue>
<type>VARIABLE</type>
</item>
</parameters>
<sources id="252" type="java.util.HashSet">
<item idref="239" type="com.j2fe.workflow.definition.Transition"/>
</sources>
<targets id="253" type="java.util.HashSet">
<item id="254" type="com.j2fe.workflow.definition.Transition">
<name id="255">goto-next</name>
<source idref="241"/>
<target id="256">
<activation>INVOKE</activation>
<clusteredCall>false</clusteredCall>
<description id="257">Automatically generated</description>
<directJoin>false</directJoin>
<name id="258">Merge</name>
<nodeHandler>com.j2fe.workflow.handler.impl.DummyActivityHandler</nodeHandler>
<nodeHandlerClass id="259">com.j2fe.workflow.handler.impl.DummyActivityHandler</nodeHandlerClass>
<sources id="260" type="java.util.HashSet">
<item idref="254" type="com.j2fe.workflow.definition.Transition"/>
<item id="261" type="com.j2fe.workflow.definition.Transition">
<name id="262">goto-next</name>
<source id="263">
<activation>TRANSACTIONAL</activation>
<clusteredCall>false</clusteredCall>
<directJoin>false</directJoin>
<name id="264">NOP  #3</name>
<nodeHandler>com.j2fe.workflow.handler.impl.DummyActivityHandler</nodeHandler>
<nodeHandlerClass id="265">com.j2fe.workflow.handler.impl.DummyActivityHandler</nodeHandlerClass>
<sources id="266" type="java.util.HashSet">
<item id="267" type="com.j2fe.workflow.definition.Transition">
<name id="268">failed Entities</name>
<source id="269">
<activation>INVOKE</activation>
<clusteredCall>false</clusteredCall>
<directJoin>false</directJoin>
<name id="270">Extract Entity</name>
<nodeHandler>com.thegoldensource.publishing.activity.ExtractBusinessEntity</nodeHandler>
<nodeHandlerClass id="271">com.thegoldensource.publishing.activity.ExtractBusinessEntity</nodeHandlerClass>
<parameters id="272" type="java.util.HashSet">
<item id="273" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="274">extractAllFieldsOfRelatedEntity</name>
<stringValue id="275">false</stringValue>
<type>CONSTANT</type>
</item>
<item id="276" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="277">extractionDefinition</name>
<stringValue id="278">ExtractDefinition</stringValue>
<type>VARIABLE</type>
</item>
<item id="279" type="com.j2fe.workflow.definition.Parameter">
<input>false</input>
<name id="280">extractionResult</name>
<stringValue id="281">ExtractionResult</stringValue>
<type>VARIABLE</type>
</item>
<item id="282" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="283">extractorFactory</name>
<stringValue id="284">DataSource</stringValue>
<type>VARIABLE</type>
</item>
</parameters>
<sources id="285" type="java.util.HashSet">
<item id="286" type="com.j2fe.workflow.definition.Transition">
<name id="287">loop</name>
<source id="288">
<activation>INVOKE</activation>
<clusteredCall>false</clusteredCall>
<directJoin>false</directJoin>
<name id="289">For Loop</name>
<nodeHandler>com.j2fe.workflow.handler.impl.ForEach</nodeHandler>
<nodeHandlerClass id="290">com.j2fe.workflow.handler.impl.ForEach</nodeHandlerClass>
<parameters id="291" type="java.util.HashSet">
<item id="292" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="293">counter</name>
<stringValue id="294">loopCounter</stringValue>
<type>VARIABLE</type>
</item>
<item id="295" type="com.j2fe.workflow.definition.Parameter">
<input>false</input>
<name id="296">counter</name>
<stringValue id="297">loopCounter</stringValue>
<type>VARIABLE</type>
</item>
<item id="298" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="299">incrementValue</name>
<objectValue id="300" type="java.lang.Integer">1</objectValue>
<type>CONSTANT</type>
</item>
<item id="301" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="302">input</name>
<stringValue id="303">BulkedItems</stringValue>
<type>VARIABLE</type>
</item>
<item id="304" type="com.j2fe.workflow.definition.Parameter">
<input>false</input>
<name id="305">output</name>
<stringValue id="306">ExtractDefinition</stringValue>
<type>VARIABLE</type>
</item>
<item id="307" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="308">resetOnEnd</name>
<objectValue id="309" type="java.lang.Boolean">true</objectValue>
<type>CONSTANT</type>
</item>
</parameters>
<sources id="310" type="java.util.HashSet">
<item id="311" type="com.j2fe.workflow.definition.Transition">
<name id="312">ToSplit</name>
<source idref="256"/>
<target idref="288"/>
</item>
</sources>
<targets id="313" type="java.util.HashSet">
<item id="314" type="com.j2fe.workflow.definition.Transition">
<name id="315">end-loop</name>
<source idref="288"/>
<target id="316">
<activation>ASYNCHRONOUS</activation>
<clusteredCall>false</clusteredCall>
<directJoin>true</directJoin>
<name id="317">Synchronize</name>
<nodeHandler>com.j2fe.workflow.handler.impl.StandardAndJoinHandler</nodeHandler>
<nodeHandlerClass id="318">com.j2fe.workflow.handler.impl.StandardAndJoinHandler</nodeHandlerClass>
<sources id="319" type="java.util.HashSet">
<item idref="314" type="com.j2fe.workflow.definition.Transition"/>
</sources>
<targets id="320" type="java.util.HashSet">
<item id="321" type="com.j2fe.workflow.definition.Transition">
<name id="322">goto-next</name>
<source idref="316"/>
<target id="323">
<activation>INVOKE</activation>
<clusteredCall>false</clusteredCall>
<directJoin>false</directJoin>
<name id="324">NOP</name>
<nodeHandler>com.j2fe.workflow.handler.impl.DummyActivityHandler</nodeHandler>
<nodeHandlerClass id="325">com.j2fe.workflow.handler.impl.DummyActivityHandler</nodeHandlerClass>
<sources id="326" type="java.util.HashSet">
<item idref="321" type="com.j2fe.workflow.definition.Transition"/>
</sources>
<targets id="327" type="java.util.HashSet">
<item id="328" type="com.j2fe.workflow.definition.Transition">
<name id="329">goto-next</name>
<source idref="323"/>
<target idref="8"/>
</item>
</targets>
<type>ACTIVITY</type>
</target>
</item>
</targets>
<type>ANDJOIN</type>
</target>
</item>
<item idref="286" type="com.j2fe.workflow.definition.Transition"/>
</targets>
<type>XORSPLIT</type>
</source>
<target idref="269"/>
</item>
</sources>
<targets id="330" type="java.util.HashSet">
<item idref="267" type="com.j2fe.workflow.definition.Transition"/>
<item id="331" type="com.j2fe.workflow.definition.Transition">
<name id="332">found Entities</name>
<source idref="269"/>
<target id="333">
<activation>INVOKE</activation>
<clusteredCall>false</clusteredCall>
<directJoin>false</directJoin>
<name id="334">Extract Vendor Business Entities</name>
<nodeHandler>com.thegoldensource.publishing.activity.ExtractVendorBusinessEntities</nodeHandler>
<nodeHandlerClass id="335">com.thegoldensource.publishing.activity.ExtractVendorBusinessEntities</nodeHandlerClass>
<parameters id="336" type="java.util.HashSet">
<item id="337" type="com.j2fe.workflow.definition.Parameter">
<input>false</input>
<name id="338">GCandVDDBEntities</name>
<stringValue id="339">GCAndVDDBEntities</stringValue>
<type>VARIABLE</type>
</item>
<item id="340" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="341">dataSource</name>
<stringValue id="342">jdbc/VDDB-1</stringValue>
<type>REFERENCE</type>
</item>
<item id="343" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="344">entities</name>
<stringValue id="345">ExtractionResult</stringValue>
<type>VARIABLE</type>
</item>
<item id="346" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="347">extractionDefinition</name>
<stringValue id="348">ExtractDefinition</stringValue>
<type>VARIABLE</type>
</item>
<item id="349" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="350">factory</name>
<stringValue id="351">jdbc/VDDB-1</stringValue>
<type>REFERENCE</type>
</item>
<item id="352" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="353">tableType</name>
<stringValue id="354">Table Type</stringValue>
<type>VARIABLE</type>
</item>
</parameters>
<persistentVariables id="355" type="java.util.HashSet">
<item id="356" type="java.lang.String">ExtractDefinition
0100</item>
<item id="357" type="java.lang.String">ExtractionResult
0100</item>
</persistentVariables>
<sources id="358" type="java.util.HashSet">
<item idref="331" type="com.j2fe.workflow.definition.Transition"/>
</sources>
<targets id="359" type="java.util.HashSet">
<item id="360" type="com.j2fe.workflow.definition.Transition">
<name id="361">found Entities</name>
<source idref="333"/>
<target id="362">
<activation>INVOKE</activation>
<clusteredCall>false</clusteredCall>
<directJoin>false</directJoin>
<name id="363">NOP  #2</name>
<nodeHandler>com.j2fe.workflow.handler.impl.DummyActivityHandler</nodeHandler>
<nodeHandlerClass id="364">com.j2fe.workflow.handler.impl.DummyActivityHandler</nodeHandlerClass>
<sources id="365" type="java.util.HashSet">
<item idref="360" type="com.j2fe.workflow.definition.Transition"/>
</sources>
<targets id="366" type="java.util.HashSet">
<item id="367" type="com.j2fe.workflow.definition.Transition">
<name id="368">goto-next</name>
<source idref="362"/>
<target id="369">
<activation>INVOKE</activation>
<clusteredCall>false</clusteredCall>
<directJoin>false</directJoin>
<name id="370">Vendor Data Compare Activity</name>
<nodeHandler>com.thegoldensource.vddbcompare.VddbCompare</nodeHandler>
<nodeHandlerClass id="371">com.thegoldensource.vddbcompare.VddbCompare</nodeHandlerClass>
<parameters id="372" type="java.util.HashSet">
<item id="373" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="374">decimalPrecision</name>
<objectValue id="375" type="java.lang.Integer">2</objectValue>
<type>CONSTANT</type>
</item>
<item id="376" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="377">entities</name>
<stringValue id="378">GCAndVDDBEntities</stringValue>
<type>VARIABLE</type>
</item>
<item id="379" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="380">extractorFactory</name>
<stringValue id="381">jdbc/GSDM-1</stringValue>
<type>REFERENCE</type>
</item>
<item id="382" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="383">lastChangeUser</name>
<stringValue id="384">lastChangeUser</stringValue>
<type>VARIABLE</type>
</item>
<item id="385" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="386">sessionFactory</name>
<stringValue id="387">jdbc/GSDM-1</stringValue>
<type>REFERENCE</type>
</item>
<item id="388" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="389">vddbDatasource</name>
<stringValue id="390">jdbc/VDDB-1</stringValue>
<type>REFERENCE</type>
</item>
</parameters>
<persistentVariables id="391" type="java.util.HashSet">
<item id="392" type="java.lang.String">GCAndVDDBEntities
0100</item>
</persistentVariables>
<sources id="393" type="java.util.HashSet">
<item idref="367" type="com.j2fe.workflow.definition.Transition"/>
</sources>
<targets id="394" type="java.util.HashSet">
<item id="395" type="com.j2fe.workflow.definition.Transition">
<name id="396">goto-next</name>
<source idref="369"/>
<target idref="263"/>
</item>
</targets>
<type>ACTIVITY</type>
</target>
</item>
</targets>
<type>ACTIVITY</type>
</target>
</item>
<item id="397" type="com.j2fe.workflow.definition.Transition">
<name id="398">no Entities</name>
<source idref="333"/>
<target idref="263"/>
</item>
</targets>
<type>XORSPLIT</type>
</target>
</item>
<item id="399" type="com.j2fe.workflow.definition.Transition">
<name id="400">no Entities</name>
<source idref="269"/>
<target idref="263"/>
</item>
</targets>
<type>XORSPLIT</type>
</source>
<target idref="263"/>
</item>
<item idref="395" type="com.j2fe.workflow.definition.Transition"/>
<item idref="399" type="com.j2fe.workflow.definition.Transition"/>
<item idref="397" type="com.j2fe.workflow.definition.Transition"/>
</sources>
<targets id="401" type="java.util.HashSet">
<item idref="261" type="com.j2fe.workflow.definition.Transition"/>
</targets>
<type>ACTIVITY</type>
</source>
<target idref="256"/>
</item>
</sources>
<targets id="402" type="java.util.HashSet">
<item idref="311" type="com.j2fe.workflow.definition.Transition"/>
</targets>
<type>ACTIVITY</type>
</target>
</item>
</targets>
<type>ACTIVITY</type>
</target>
</item>
</targets>
<type>ANDSPLIT</type>
</target>
</item>
</targets>
<type>ACTIVITY</type>
</target>
</item>
<item id="403" type="com.j2fe.workflow.definition.Transition">
<name id="404">no-extraction</name>
<source idref="160"/>
<target idref="8"/>
</item>
</targets>
<type>XORSPLIT</type>
</target>
</item>
<item id="405" type="com.j2fe.workflow.definition.Transition">
<name id="406">no entities</name>
<source idref="125"/>
<target idref="8"/>
</item>
</targets>
<type>XORSPLIT</type>
</target>
</item>
</targets>
<type>XORSPLIT</type>
</source>
<target idref="8"/>
</item>
<item idref="328" type="com.j2fe.workflow.definition.Transition"/>
<item idref="120" type="com.j2fe.workflow.definition.Transition"/>
<item idref="405" type="com.j2fe.workflow.definition.Transition"/>
<item idref="403" type="com.j2fe.workflow.definition.Transition"/>
</sources>
<targets id="407" type="java.util.HashSet">
<item idref="6" type="com.j2fe.workflow.definition.Transition"/>
</targets>
<type>ACTIVITY</type>
</source>
<target idref="2"/>
</item>
</sources>
<targets id="408" type="java.util.HashSet"/>
<type>END</type>
</endNode>
<forcePurgeAtEnd>true</forcePurgeAtEnd>
<group id="409">RMBCustom</group>
<haltOnError>false</haltOnError>
<lastChangeUser id="410">user1</lastChangeUser>
<lastUpdate id="411">2020-08-22T05:18:45.000+0200</lastUpdate>
<name id="412">RMBPVDDBCompare</name>
<optimize>true</optimize>
<parameter id="413" type="java.util.HashMap">
<entry>
<key id="414" type="java.lang.String">BulkSize</key>
<value id="415" type="com.j2fe.workflow.definition.WorkflowParameter">
<className id="416">java.lang.Integer</className>
<clazz>java.lang.Integer</clazz>
<description id="417">No of Entities to be processed in a bulk</description>
<input>true</input>
<output>false</output>
<required>true</required>
</value>
</entry>
<entry>
<key id="418" type="java.lang.String">Candidate Filter</key>
<value id="419" type="com.j2fe.workflow.definition.WorkflowParameter">
<className id="420">java.lang.String</className>
<clazz>java.lang.String</clazz>
<input>true</input>
<output>false</output>
<required>false</required>
</value>
</entry>
<entry>
<key id="421" type="java.lang.String">DataSource</key>
<value id="422" type="com.j2fe.workflow.definition.WorkflowParameter">
<className id="423">java.lang.String</className>
<clazz>java.lang.String</clazz>
<description id="424">Database</description>
<input>true</input>
<output>false</output>
<required>true</required>
</value>
</entry>
<entry>
<key id="425" type="java.lang.String">GSO Name</key>
<value id="426" type="com.j2fe.workflow.definition.WorkflowParameter">
<className id="427">java.lang.String</className>
<clazz>java.lang.String</clazz>
<input>true</input>
<output>false</output>
<required>false</required>
</value>
</entry>
<entry>
<key id="428" type="java.lang.String">NumberOfThreads</key>
<value id="429" type="com.j2fe.workflow.definition.WorkflowParameter">
<className id="430">java.lang.Integer</className>
<clazz>java.lang.Integer</clazz>
<input>true</input>
<output>false</output>
<required>true</required>
</value>
</entry>
<entry>
<key id="431" type="java.lang.String">Table Type</key>
<value id="432" type="com.j2fe.workflow.definition.WorkflowParameter">
<className id="433">java.lang.String</className>
<clazz>java.lang.String</clazz>
<input>true</input>
<output>false</output>
<required>false</required>
</value>
</entry>
<entry>
<key id="434" type="java.lang.String">UI Template Name</key>
<value id="435" type="com.j2fe.workflow.definition.WorkflowParameter">
<className id="436">java.lang.String</className>
<clazz>java.lang.String</clazz>
<input>true</input>
<output>false</output>
<required>false</required>
</value>
</entry>
</parameter>
<permissions id="437" type="java.util.HashSet"/>
<priority>50</priority>
<purgeAtEnd>true</purgeAtEnd>
<retries>0</retries>
<startNode idref="102"/>
<status>RELEASED</status>
<variables id="438" type="java.util.HashMap">
<entry>
<key id="439" type="java.lang.String">BulkSize</key>
<value id="440" type="com.j2fe.workflow.definition.GlobalVariable">
<className id="441">java.lang.Integer</className>
<clazz>java.lang.Integer</clazz>
<description id="442">No of Entities to be processed in a bulk</description>
<persistent>false</persistent>
<value id="443" type="java.lang.Integer">20</value>
</value>
</entry>
<entry>
<key id="444" type="java.lang.String">Candidate Filter</key>
<value id="445" type="com.j2fe.workflow.definition.GlobalVariable">
<className id="446">java.lang.String</className>
<clazz>java.lang.String</clazz>
<persistent>false</persistent>
</value>
</entry>
<entry>
<key id="447" type="java.lang.String">Counter</key>
<value id="448" type="com.j2fe.workflow.definition.GlobalVariable">
<className id="449">java.lang.Integer</className>
<clazz>java.lang.Integer</clazz>
<persistent>false</persistent>
<value id="450" type="java.lang.Integer">0</value>
</value>
</entry>
<entry>
<key id="451" type="java.lang.String">DataSource</key>
<value id="452" type="com.j2fe.workflow.definition.GlobalVariable">
<className id="453">java.lang.String</className>
<clazz>java.lang.String</clazz>
<description id="454">Database</description>
<persistent>false</persistent>
<value id="455" type="java.lang.String">jdbc/GSDM-1</value>
</value>
</entry>
<entry>
<key id="456" type="java.lang.String">GSO Name</key>
<value id="457" type="com.j2fe.workflow.definition.GlobalVariable">
<className id="458">java.lang.String</className>
<clazz>java.lang.String</clazz>
<persistent>false</persistent>
</value>
</entry>
<entry>
<key id="459" type="java.lang.String">NumberOfThreads</key>
<value id="460" type="com.j2fe.workflow.definition.GlobalVariable">
<className id="461">java.lang.Integer</className>
<clazz>java.lang.Integer</clazz>
<persistent>true</persistent>
<value id="462" type="java.lang.Integer">10</value>
</value>
</entry>
<entry>
<key id="463" type="java.lang.String">Table Type</key>
<value id="464" type="com.j2fe.workflow.definition.GlobalVariable">
<className id="465">java.lang.String</className>
<clazz>java.lang.String</clazz>
<persistent>false</persistent>
</value>
</entry>
<entry>
<key id="466" type="java.lang.String">UI Template Name</key>
<value id="467" type="com.j2fe.workflow.definition.GlobalVariable">
<className id="468">java.lang.String</className>
<clazz>java.lang.String</clazz>
<persistent>false</persistent>
</value>
</entry>
</variables>
<version>6</version>
</com.j2fe.workflow.definition.Workflow>
</businessobject>
</goldensource-package>
