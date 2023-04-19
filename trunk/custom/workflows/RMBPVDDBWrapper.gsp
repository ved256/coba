<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<goldensource-package version="8.7.1.62">
<package-comment/>
<businessobject displayString="66 - 8.7.1.29.20" type="com.j2fe.workflow.definition.Workflow">
<com.j2fe.workflow.definition.Workflow id="0">
<alwaysPersist>false</alwaysPersist>
<clustered>true</clustered>
<comment id="1">8.7.1.29.20</comment>
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
<name id="9">Ignore Irrelevant Records</name>
<nodeHandler>com.j2fe.general.activities.database.DBStatement</nodeHandler>
<nodeHandlerClass id="10">com.j2fe.general.activities.database.DBStatement</nodeHandlerClass>
<parameters id="11" type="java.util.HashSet">
<item id="12" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="13">database</name>
<stringValue id="14">jdbc/GSDM-1</stringValue>
<type>REFERENCE</type>
</item>
<item id="15" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="16">querySQL</name>
<objectValue id="17" type="java.lang.String">/* Formatted on 04-05-2020 16:04:09 (QP5 v5.227.12220.39754) */&#13;
DECLARE&#13;
   CURSOR curDistinctVDCDSingle&#13;
   IS&#13;
      SELECT DISTINCT VDCD_OID, GSO_FLD_NME&#13;
        FROM FT_T_VDCP&#13;
       WHERE     PRT_STAT_TYP &lt;&gt; 'IGNORE'&#13;
             AND PRIM_KEY_TXT IS NULL;&#13;
           --  AND VDCD_OID = '/j6F110IG1';&#13;
&#13;
   CURSOR curDistinctVDCDMulti&#13;
   IS&#13;
      SELECT DISTINCT VDCD_OID, GSO_FLD_NME, PRIM_KEY_TXT&#13;
        FROM FT_T_VDCP&#13;
       WHERE     PRT_STAT_TYP &lt;&gt; 'IGNORE'&#13;
             AND PRIM_KEY_TXT IS NOT NULL;&#13;
           --  AND VDCD_OID = '/j6F110IG1';&#13;
&#13;
   vGCData       FT_T_VDCP.VND_DATA_TXT%TYPE;&#13;
   vMismatch     CHAR (1);&#13;
   vMatchCount   NUMBER (3);&#13;
BEGIN&#13;
   --Ignore Irrelevent Feilds - Removed the feilds from the template so logic is no longer required&#13;
   UPDATE FT_T_VDCP&#13;
      SET PRT_STAT_TYP = 'IGNORE',&#13;
          LAST_CHG_TMS = SYSDATE,&#13;
          LAST_CHG_USR_ID = 'IGNORE-IRRELEVENT'&#13;
    WHERE     GSO_FLD_NME IN&#13;
                 ('RMBPDepartment.RMBPSubDivSummary.RMBPPrimeBroking',&#13;
                  'RMBPDepartment.RMBPSubDivSummary.RMBPDepartmentWorkflowCategory',&#13;
                  'RMBPDepartment.RMBPSubDivSummary.RMBPDepartmentGsId')&#13;
                  --'RMBPPortfolio.RMBPPortfolioSummary.RMBPLastChangeUser',&#13;
                  --'RMBPDepartment.RMBPSubDivSummary.RMBPDepartmentLastChangeTms',&#13;
                  --'RMBPDepartment.RMBPSubDivSummary.RMBPDepartmentLastChangeUser',&#13;
                  --'RMBPDepartment.RMBPSubDivSummary.SUBDMAINStartDateTime')&#13;
          AND PRT_STAT_TYP &lt;&gt; 'IGNORE';&#13;
&#13;
   --Legal Hieracrhy Replace ID with Name Path String&#13;
   UPDATE FT_T_VDCP&#13;
      SET VND_DATA_TXT =&#13;
             (SELECT    LEGAL_ENTITY_NME&#13;
                     || '|'&#13;
                     || SUPER_SEGMENT_NME&#13;
                     || '|'&#13;
                     || BUSINESS_UNIT_NME&#13;
                     || '|'&#13;
                     || FOCUS_GROUP_NME&#13;
                     || '|'&#13;
                     || TRADING_AREA_NME&#13;
                     || '|'&#13;
                     || DEPARTMENT_NME&#13;
                     || '|'&#13;
                     || SUB_TRADING_AREA_NME&#13;
                     || '|'&#13;
                     || LEGAL_ENTITY1_NME&#13;
                     || '|'&#13;
                     || LEGAL_ENTITY2_NME&#13;
                     || '|'&#13;
                     || LEGAL_ENTITY3_NME&#13;
                FROM FT_T_LGH1&#13;
               WHERE LGL_HRCHY_OID = VND_DATA_TXT),&#13;
          LAST_CHG_TMS = SYSDATE,&#13;
          LAST_CHG_USR_ID = 'LEGAL-HIERARCHY-CHANGE'&#13;
    WHERE     GSO_FLD_NME LIKE '%RMBPPortfolioLegalHierarchy%'&#13;
          AND PRT_STAT_TYP &lt;&gt; 'IGNORE'&#13;
          AND VND_DATA_TXT IS NOT NULL&#13;
          AND REGEXP_LIKE(VND_DATA_TXT, '^[0-9]');&#13;
&#13;
   ----Legal HieracrhyDepartment Replace ID with Name Path String&#13;
   UPDATE FT_T_VDCP&#13;
      SET VND_DATA_TXT =&#13;
             (SELECT    LEGAL_ENTITY_NME&#13;
                     || '|'&#13;
                     || SUPER_SEGMENT_NME&#13;
                     || '|'&#13;
                     || BUSINESS_UNIT_NME&#13;
                     || '|'&#13;
                     || FOCUS_GROUP_NME&#13;
                     || '|'&#13;
                     || TRADING_AREA_NME&#13;
                FROM FT_T_LGD1&#13;
               WHERE LGL_HRCHY_DEPT_OID = VND_DATA_TXT),&#13;
          LAST_CHG_TMS = SYSDATE,&#13;
          LAST_CHG_USR_ID = 'LEGAL-HIERARCHY-DEPT-CHANGE'&#13;
    WHERE     GSO_FLD_NME LIKE '%RMBPLegalHierarchyDepartment%'&#13;
          AND PRT_STAT_TYP &lt;&gt; 'IGNORE'&#13;
          AND VND_DATA_TXT IS NOT NULL&#13;
          AND REGEXP_LIKE(VND_DATA_TXT, '^[0-9]');&#13;
&#13;
   ----Risk Hieracrhy Replace ID with Name Path String&#13;
   UPDATE FT_T_VDCP&#13;
      SET VND_DATA_TXT =&#13;
             (SELECT    ENTITY_SET_NME&#13;
                     || '|'&#13;
                     || CLOSING_ENTITY_NME&#13;
                     || '|'&#13;
                     || OPERATING_ENTITY_NME&#13;
                     || '|'&#13;
                     || BUSINESS_UNIT_NME&#13;
                     || '|'&#13;
                     || PRODUCT_HOUSE_NME&#13;
                     || '|'&#13;
                     || STRATEGY_NME&#13;
                     || '|'&#13;
                     || SUB_STRATEGY_NME&#13;
                     || '|'&#13;
                     || CORE_PRODUCT_STRATEGY_NME&#13;
                FROM FT_T_RKH1&#13;
               WHERE RISK_HRCHY_OID = VND_DATA_TXT),&#13;
          LAST_CHG_TMS = SYSDATE,&#13;
          LAST_CHG_USR_ID = 'RISK-HIERARCHY-CHANGE'&#13;
    WHERE     GSO_FLD_NME LIKE '%RMBPPortfolioRiskHierarchy%'&#13;
          AND PRT_STAT_TYP &lt;&gt; 'IGNORE'&#13;
          AND VND_DATA_TXT IS NOT NULL&#13;
          AND REGEXP_LIKE(VND_DATA_TXT, '^[0-9]');&#13;
   &#13;
   --Management Hieracrhy Replace ID with Name Path String       &#13;
   UPDATE FT_T_VDCP&#13;
      SET VND_DATA_TXT =&#13;
             (SELECT    SUPER_SEGMENT_NME&#13;
                     || '|'&#13;
                     || SUPER_SEGMENT1_NME&#13;
                     || '|'&#13;
                     || BUSINESS_UNIT_NME&#13;
                     || '|'&#13;
                     || MANAGEMENT_ROLLUP_NME&#13;
                     || '|'&#13;
                     || FOCUS_GROUP_NME&#13;
                     || '|'&#13;
                     || FOCUS_GROUP1_NME&#13;
                     || '|'&#13;
                     || DESK_NME&#13;
                     || '|'&#13;
                     || SUB_DESK_NME&#13;
                     || '|'&#13;
                     || MANAGEMENT_DEPT_NME&#13;
                     || '|'&#13;
                     || FAS_GROUP_CODE_NME&#13;
                     || '|'&#13;
                     || STRATEGY_NME&#13;
                     || '|'&#13;
                     || SUB_TRADING_AREA_NME&#13;
                     || '|'&#13;
                     || DEPARTMENT_NME&#13;
                     || '|'&#13;
                     || DEPARTMENT1_NME&#13;
                     || '|'&#13;
                     || TRADING_AREA_NME&#13;
                     || '|'&#13;
                     || LEGAL_ENTITY_NME&#13;
                     || '|'&#13;
                     || LEGAL_ENTITY1_NME&#13;
                     || '|'&#13;
                     || LEGAL_ENTITY2_NME&#13;
                FROM FT_T_MGH1&#13;
               WHERE NODE_ID_PATH = VND_DATA_TXT),&#13;
          LAST_CHG_TMS = SYSDATE,&#13;
          LAST_CHG_USR_ID = 'MGMT-HIERARCHY-CHANGE'&#13;
    WHERE     GSO_FLD_NME LIKE '%RMBPManagementHierarchy.%'&#13;
          AND PRT_STAT_TYP &lt;&gt; 'IGNORE'&#13;
          AND VND_DATA_TXT LIKE '%~%'&#13;
          AND VND_DATA_TXT IS NOT NULL;&#13;
          &#13;
   --Management Hieracrhy Replace ID with Name Path String       &#13;
   UPDATE FT_T_VDCP&#13;
      SET VND_DATA_TXT =&#13;
             (SELECT    SUPER_SEGMENT_NME&#13;
                     || '|'&#13;
                     || SUPER_SEGMENT1_NME&#13;
                     || '|'&#13;
                     || BUSINESS_UNIT_NME&#13;
                     || '|'&#13;
                     || MANAGEMENT_ROLLUP_NME&#13;
                     || '|'&#13;
                     || FOCUS_GROUP_NME&#13;
                     || '|'&#13;
                     || FOCUS_GROUP1_NME&#13;
                     || '|'&#13;
                     || DESK_NME&#13;
                     || '|'&#13;
                     || SUB_DESK_NME&#13;
                     || '|'&#13;
                     || MANAGEMENT_DEPT_NME&#13;
                     || '|'&#13;
                     || FAS_GROUP_CODE_NME&#13;
                     || '|'&#13;
                     || STRATEGY_NME&#13;
                     || '|'&#13;
                     || SUB_TRADING_AREA_NME&#13;
                     || '|'&#13;
                     || DEPARTMENT_NME&#13;
                     || '|'&#13;
                     || DEPARTMENT1_NME&#13;
                     || '|'&#13;
                     || TRADING_AREA_NME&#13;
                     || '|'&#13;
                     || LEGAL_ENTITY_NME&#13;
                     || '|'&#13;
                     || LEGAL_ENTITY1_NME&#13;
                     || '|'&#13;
                     || LEGAL_ENTITY2_NME&#13;
                FROM FT_T_MGD1&#13;
               WHERE NODE_ID_PATH = VND_DATA_TXT),&#13;
          LAST_CHG_TMS = SYSDATE,&#13;
          LAST_CHG_USR_ID = 'MGMT-HIERARCHY-DEPT-CHANGE'&#13;
    WHERE     GSO_FLD_NME LIKE '%RMBPMgmtHierarchyDepRELVDComp.%'&#13;
          AND PRT_STAT_TYP &lt;&gt; 'IGNORE'&#13;
          AND VND_DATA_TXT LIKE '%~%'&#13;
          AND VND_DATA_TXT IS NOT NULL;&#13;
&#13;
   FOR X IN curDistinctVDCDSingle&#13;
   LOOP&#13;
      BEGIN&#13;
         SELECT VND_DATA_TXT&#13;
           INTO vGCData&#13;
           FROM FT_T_VDCP&#13;
          WHERE     PRT_STAT_TYP &lt;&gt; 'IGNORE'&#13;
                AND VDCD_OID = X.VDCD_OID&#13;
                AND GSO_FLD_NME = X.GSO_FLD_NME&#13;
                AND DATA_SRC_ID = 'GC';&#13;
      EXCEPTION&#13;
         WHEN NO_DATA_FOUND&#13;
         THEN&#13;
            vGCData := NULL;&#13;
      END;&#13;
&#13;
      vMismatch := 'N';&#13;
      vMatchCount := 0;&#13;
&#13;
      FOR Y&#13;
         IN (SELECT DATA_SRC_ID,&#13;
                    GSO_FLD_NME,&#13;
                    VND_DATA_TXT,&#13;
                    PRIM_KEY_TXT,&#13;
                    VDCD_OID,&#13;
                    VDCP_OID&#13;
               FROM FT_T_VDCP&#13;
              WHERE     PRT_STAT_TYP &lt;&gt; 'IGNORE'&#13;
                    AND VDCD_OID = X.VDCD_OID&#13;
                    AND GSO_FLD_NME = X.GSO_FLD_NME&#13;
                    AND DATA_SRC_ID &lt;&gt; 'GC')&#13;
      LOOP&#13;
         IF Y.VND_DATA_TXT IS NULL&#13;
         THEN&#13;
            --Ignore Blank VDCP Entries&#13;
            UPDATE FT_T_VDCP&#13;
               SET PRT_STAT_TYP = 'IGNORE',&#13;
                   LAST_CHG_TMS = SYSDATE,&#13;
                   LAST_CHG_USR_ID = 'IGNORE-NULL-SINGLE'&#13;
             WHERE VDCP_OID = Y.VDCP_OID;&#13;
         ELSIF upper(Y.VND_DATA_TXT) = upper(vGCData)&#13;
         THEN&#13;
            --IGNORE GC and VDDB Matches&#13;
            UPDATE FT_T_VDCP&#13;
               SET PRT_STAT_TYP = 'IGNORE',&#13;
                   LAST_CHG_TMS = SYSDATE,&#13;
                   LAST_CHG_USR_ID = 'IGNORE-MATCH-SRC-SINGLE'&#13;
             WHERE VDCP_OID = Y.VDCP_OID;&#13;
&#13;
            vMatchCount := vMatchCount + 1;&#13;
            &#13;
         ELSIF    upper(Y.VND_DATA_TXT) &lt;&gt; upper(vGCData)&#13;
               OR (Y.VND_DATA_TXT IS NOT NULL AND vGCData IS NULL)&#13;
         THEN&#13;
            vMismatch := 'Y';&#13;
         END IF;&#13;
&#13;
         --DBMS_OUTPUT.put_line (Y.VND_DATA_TXT || ' - ' || vGCData || ' - ' || vMatchCount);&#13;
      END LOOP;&#13;
&#13;
      --DBMS_OUTPUT.put_line (X.GSO_FLD_NME || '-' || vMismatch);&#13;
&#13;
      --IGNORE GC if it matches to all VDDB entries&#13;
      IF vMismatch = 'N' AND vGCData IS NOT NULL AND vMatchCount = 0&#13;
      THEN&#13;
         vMismatch := 'Y';&#13;
      ELSIF vMismatch = 'N'&#13;
      THEN&#13;
         UPDATE FT_T_VDCP&#13;
            SET PRT_STAT_TYP = 'IGNORE',&#13;
                LAST_CHG_TMS = SYSDATE,&#13;
                LAST_CHG_USR_ID = 'IGNORE-MATCH-GC-SINGLE'&#13;
          WHERE     PRT_STAT_TYP &lt;&gt; 'IGNORE'&#13;
                AND VDCD_OID = X.VDCD_OID&#13;
                AND GSO_FLD_NME = X.GSO_FLD_NME&#13;
                AND DATA_SRC_ID = 'GC';&#13;
      END IF;&#13;
&#13;
    --  COMMIT;&#13;
   END LOOP;&#13;
&#13;
   FOR X IN curDistinctVDCDMulti&#13;
   LOOP&#13;
      BEGIN&#13;
         SELECT VND_DATA_TXT&#13;
           INTO vGCData&#13;
           FROM FT_T_VDCP&#13;
          WHERE     PRT_STAT_TYP &lt;&gt; 'IGNORE'&#13;
                AND VDCD_OID = X.VDCD_OID&#13;
                AND GSO_FLD_NME = X.GSO_FLD_NME&#13;
                AND PRIM_KEY_TXT = X.PRIM_KEY_TXT&#13;
                AND DATA_SRC_ID = 'GC';&#13;
      EXCEPTION&#13;
         WHEN NO_DATA_FOUND&#13;
         THEN&#13;
            vGCData := NULL;&#13;
      END;&#13;
&#13;
      vMismatch := 'N';&#13;
      vMatchCount := 0;&#13;
&#13;
      FOR Y&#13;
         IN (SELECT DATA_SRC_ID,&#13;
                    GSO_FLD_NME,&#13;
                    VND_DATA_TXT,&#13;
                    PRIM_KEY_TXT,&#13;
                    VDCD_OID,&#13;
                    VDCP_OID&#13;
               FROM FT_T_VDCP&#13;
              WHERE     PRT_STAT_TYP &lt;&gt; 'IGNORE'&#13;
                    AND VDCD_OID = X.VDCD_OID&#13;
                    AND GSO_FLD_NME = X.GSO_FLD_NME&#13;
                    AND PRIM_KEY_TXT = X.PRIM_KEY_TXT&#13;
                    AND DATA_SRC_ID &lt;&gt; 'GC')&#13;
      LOOP&#13;
         IF Y.VND_DATA_TXT IS NULL&#13;
         THEN&#13;
            --Ignore Blank VDCP Entries&#13;
            UPDATE FT_T_VDCP&#13;
               SET PRT_STAT_TYP = 'IGNORE',&#13;
                   LAST_CHG_TMS = SYSDATE,&#13;
                   LAST_CHG_USR_ID = 'IGNORE-NULL-MULTI'&#13;
             WHERE VDCP_OID = Y.VDCP_OID;&#13;
         ELSIF upper(Y.VND_DATA_TXT) = upper(vGCData)&#13;
         THEN&#13;
            --IGNORE GC and VDDB Matches&#13;
            UPDATE FT_T_VDCP&#13;
               SET PRT_STAT_TYP = 'IGNORE',&#13;
                   LAST_CHG_TMS = SYSDATE,&#13;
                   LAST_CHG_USR_ID = 'IGNORE-MATCH-SRC-MULTI'&#13;
             WHERE VDCP_OID = Y.VDCP_OID;&#13;
             &#13;
             vMatchCount := vMatchCount + 1;&#13;
             &#13;
         ELSIF    upper(Y.VND_DATA_TXT) &lt;&gt; upper(vGCData)&#13;
               OR (Y.VND_DATA_TXT IS NOT NULL AND vGCData IS NULL)&#13;
         THEN&#13;
            vMismatch := 'Y';&#13;
         --DBMS_OUTPUT.put_line (Y.VND_DATA_TXT);&#13;
         END IF;&#13;
      END LOOP;&#13;
&#13;
      -- DBMS_OUTPUT.put_line (X.GSO_FLD_NME || '-' || vMismatch);&#13;
&#13;
      --IGNORE GC if it matches to all VDDB entries&#13;
      IF vMismatch = 'N' AND vGCData IS NOT NULL AND vMatchCount = 0&#13;
      THEN&#13;
         vMismatch := 'Y';&#13;
      ELSIF vMismatch = 'N'&#13;
      THEN&#13;
         UPDATE FT_T_VDCP&#13;
            SET PRT_STAT_TYP = 'IGNORE',&#13;
                LAST_CHG_TMS = SYSDATE,&#13;
                LAST_CHG_USR_ID = 'IGNORE-MATCH-GC-MULTI'&#13;
          WHERE     PRT_STAT_TYP &lt;&gt; 'IGNORE'&#13;
                AND VDCD_OID = X.VDCD_OID&#13;
                AND GSO_FLD_NME = X.GSO_FLD_NME&#13;
                AND PRIM_KEY_TXT = X.PRIM_KEY_TXT&#13;
                AND DATA_SRC_ID = 'GC';&#13;
      END IF;&#13;
&#13;
    --  COMMIT;&#13;
   END LOOP;&#13;
END;</objectValue>
<type>CONSTANT</type>
</item>
</parameters>
<sources id="18" type="java.util.HashSet">
<item id="19" type="com.j2fe.workflow.definition.Transition">
<name id="20">goto-next</name>
<source id="21">
<activation>INVOKE</activation>
<clusteredCall>false</clusteredCall>
<directJoin>false</directJoin>
<name id="22">VDDB Compare</name>
<nodeHandler>com.j2fe.workflow.handler.impl.CallSubWorkflow</nodeHandler>
<nodeHandlerClass id="23">com.j2fe.workflow.handler.impl.CallSubWorkflow</nodeHandlerClass>
<parameters id="24" type="java.util.HashSet">
<item id="25" type="com.j2fe.workflow.definition.Parameter">
<UITypeHint id="26">["BulkSize"]@java/lang/Integer@</UITypeHint>
<input>true</input>
<name id="27">input["BulkSize"]</name>
<stringValue id="28">BulkSize</stringValue>
<type>VARIABLE</type>
</item>
<item id="29" type="com.j2fe.workflow.definition.Parameter">
<UITypeHint id="30">["Candidate Filter"]@java/lang/String@</UITypeHint>
<input>true</input>
<name id="31">input["Candidate Filter"]</name>
<stringValue id="32">Candidate Filter</stringValue>
<type>VARIABLE</type>
</item>
<item id="33" type="com.j2fe.workflow.definition.Parameter">
<UITypeHint id="34">["DataSource"]@java/lang/String@</UITypeHint>
<input>true</input>
<name id="35">input["DataSource"]</name>
<stringValue id="36">DataSource</stringValue>
<type>VARIABLE</type>
</item>
<item id="37" type="com.j2fe.workflow.definition.Parameter">
<UITypeHint id="38">["GSO Name"]@java/lang/String@</UITypeHint>
<input>true</input>
<name id="39">input["GSO Name"]</name>
<stringValue id="40">GSO Name</stringValue>
<type>VARIABLE</type>
</item>
<item id="41" type="com.j2fe.workflow.definition.Parameter">
<UITypeHint id="42">["NumberOfThreads"]@java/lang/Integer@</UITypeHint>
<input>true</input>
<name id="43">input["NumberOfThreads"]</name>
<stringValue id="44">NumberOfThreads</stringValue>
<type>VARIABLE</type>
</item>
<item id="45" type="com.j2fe.workflow.definition.Parameter">
<UITypeHint id="46">["Table Type"]@java/lang/String@</UITypeHint>
<input>true</input>
<name id="47">input["Table Type"]</name>
<stringValue id="48">Table Type</stringValue>
<type>VARIABLE</type>
</item>
<item id="49" type="com.j2fe.workflow.definition.Parameter">
<UITypeHint id="50">["UI Template Name"]@java/lang/String@</UITypeHint>
<input>true</input>
<name id="51">input["UI Template Name"]</name>
<stringValue id="52">UI Template Name</stringValue>
<type>VARIABLE</type>
</item>
<item id="53" type="com.j2fe.workflow.definition.Parameter">
<input>true</input>
<name id="54">name</name>
<stringValue id="55">RMBPVDDBCompare</stringValue>
<type>CONSTANT</type>
</item>
</parameters>
<sources id="56" type="java.util.HashSet">
<item id="57" type="com.j2fe.workflow.definition.Transition">
<name id="58">goto-next</name>
<source id="59">
<activation>INVOKE</activation>
<clusteredCall>false</clusteredCall>
<directJoin>false</directJoin>
<name id="60">Start</name>
<nodeHandler>com.j2fe.workflow.handler.impl.DummyActivityHandler</nodeHandler>
<nodeHandlerClass id="61">com.j2fe.workflow.handler.impl.DummyActivityHandler</nodeHandlerClass>
<sources id="62" type="java.util.HashSet"/>
<targets id="63" type="java.util.HashSet">
<item idref="57" type="com.j2fe.workflow.definition.Transition"/>
</targets>
<type>START</type>
</source>
<target idref="21"/>
</item>
</sources>
<targets id="64" type="java.util.HashSet">
<item idref="19" type="com.j2fe.workflow.definition.Transition"/>
</targets>
<type>ACTIVITY</type>
</source>
<target idref="8"/>
</item>
</sources>
<targets id="65" type="java.util.HashSet">
<item idref="6" type="com.j2fe.workflow.definition.Transition"/>
</targets>
<type>ACTIVITY</type>
</source>
<target idref="2"/>
</item>
</sources>
<targets id="66" type="java.util.HashSet"/>
<type>END</type>
</endNode>
<forcePurgeAtEnd>true</forcePurgeAtEnd>
<group id="67">RMBCustom</group>
<haltOnError>false</haltOnError>
<lastChangeUser id="68">user1</lastChangeUser>
<lastUpdate id="69">2020-08-05T12:28:35.000+0530</lastUpdate>
<name id="70">RMBPVDDBWrapper</name>
<optimize>true</optimize>
<parameter id="71" type="java.util.HashMap">
<entry>
<key id="72" type="java.lang.String">BulkSize</key>
<value id="73" type="com.j2fe.workflow.definition.WorkflowParameter">
<className id="74">java.lang.Integer</className>
<clazz>java.lang.Integer</clazz>
<input>true</input>
<output>true</output>
<required>true</required>
</value>
</entry>
<entry>
<key id="75" type="java.lang.String">Candidate Filter</key>
<value id="76" type="com.j2fe.workflow.definition.WorkflowParameter">
<className id="77">java.lang.String</className>
<clazz>java.lang.String</clazz>
<input>true</input>
<output>false</output>
<required>false</required>
</value>
</entry>
<entry>
<key id="78" type="java.lang.String">DataSource</key>
<value id="79" type="com.j2fe.workflow.definition.WorkflowParameter">
<className id="80">java.lang.String</className>
<clazz>java.lang.String</clazz>
<description id="81">Database</description>
<input>true</input>
<output>false</output>
<required>true</required>
</value>
</entry>
<entry>
<key id="82" type="java.lang.String">GSO Name</key>
<value id="83" type="com.j2fe.workflow.definition.WorkflowParameter">
<className id="84">java.lang.String</className>
<clazz>java.lang.String</clazz>
<input>true</input>
<output>false</output>
<required>false</required>
</value>
</entry>
<entry>
<key id="85" type="java.lang.String">NumberOfThreads</key>
<value id="86" type="com.j2fe.workflow.definition.WorkflowParameter">
<className id="87">java.lang.Integer</className>
<clazz>java.lang.Integer</clazz>
<input>true</input>
<output>true</output>
<required>true</required>
</value>
</entry>
<entry>
<key id="88" type="java.lang.String">Table Type</key>
<value id="89" type="com.j2fe.workflow.definition.WorkflowParameter">
<className id="90">java.lang.String</className>
<clazz>java.lang.String</clazz>
<input>true</input>
<output>false</output>
<required>false</required>
</value>
</entry>
<entry>
<key id="91" type="java.lang.String">UI Template Name</key>
<value id="92" type="com.j2fe.workflow.definition.WorkflowParameter">
<className id="93">java.lang.String</className>
<clazz>java.lang.String</clazz>
<input>true</input>
<output>false</output>
<required>false</required>
</value>
</entry>
</parameter>
<permissions id="94" type="java.util.HashSet"/>
<priority>50</priority>
<purgeAtEnd>true</purgeAtEnd>
<retries>0</retries>
<startNode idref="59"/>
<status>RELEASED</status>
<variables id="95" type="java.util.HashMap">
<entry>
<key id="96" type="java.lang.String">BulkSize</key>
<value id="97" type="com.j2fe.workflow.definition.GlobalVariable">
<className id="98">java.lang.Integer</className>
<clazz>java.lang.Integer</clazz>
<persistent>true</persistent>
<value id="99" type="java.lang.Integer">20</value>
</value>
</entry>
<entry>
<key id="100" type="java.lang.String">Candidate Filter</key>
<value id="101" type="com.j2fe.workflow.definition.GlobalVariable">
<className id="102">java.lang.String</className>
<clazz>java.lang.String</clazz>
<persistent>false</persistent>
</value>
</entry>
<entry>
<key id="103" type="java.lang.String">DataSource</key>
<value id="104" type="com.j2fe.workflow.definition.GlobalVariable">
<className id="105">java.lang.String</className>
<clazz>java.lang.String</clazz>
<description id="106">Database</description>
<persistent>false</persistent>
<value id="107" type="java.lang.String">jdbc/GSDM-1</value>
</value>
</entry>
<entry>
<key id="108" type="java.lang.String">GSO Name</key>
<value id="109" type="com.j2fe.workflow.definition.GlobalVariable">
<className id="110">java.lang.String</className>
<clazz>java.lang.String</clazz>
<persistent>false</persistent>
</value>
</entry>
<entry>
<key id="111" type="java.lang.String">NumberOfThreads</key>
<value id="112" type="com.j2fe.workflow.definition.GlobalVariable">
<className id="113">java.lang.Integer</className>
<clazz>java.lang.Integer</clazz>
<persistent>true</persistent>
<value id="114" type="java.lang.Integer">10</value>
</value>
</entry>
<entry>
<key id="115" type="java.lang.String">Table Type</key>
<value id="116" type="com.j2fe.workflow.definition.GlobalVariable">
<className id="117">java.lang.String</className>
<clazz>java.lang.String</clazz>
<persistent>false</persistent>
</value>
</entry>
<entry>
<key id="118" type="java.lang.String">UI Template Name</key>
<value id="119" type="com.j2fe.workflow.definition.GlobalVariable">
<className id="120">java.lang.String</className>
<clazz>java.lang.String</clazz>
<persistent>false</persistent>
</value>
</entry>
</variables>
<version>66</version>
</com.j2fe.workflow.definition.Workflow>
</businessobject>
</goldensource-package>
