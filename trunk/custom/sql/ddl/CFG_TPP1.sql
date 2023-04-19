DECLARE
   objExists   CHAR (1);
BEGIN
   BEGIN
      SELECT 'Y'
        INTO objExists
        FROM tab
       WHERE tname = 'CFG_TPP1';
   EXCEPTION
      WHEN NO_DATA_FOUND
      THEN
         objExists := 'N';
   END;

   IF objExists = 'N'
   THEN
      EXECUTE IMMEDIATE ('
        CREATE TABLE CFG_TPP1
		(
		  DATA_TYP_TXT  VARCHAR2(40 BYTE) NOT NULL,
		  IDTR_TYP_TXT  VARCHAR2(40 BYTE) NOT NULL,
		  IDTR_TYP_VAL  VARCHAR2(40 BYTE) NOT NULL
		)
');

   ELSE
      DBMS_OUTPUT.put_line ('Table CFG_TPP1 already exists');
   END IF;
END;
/