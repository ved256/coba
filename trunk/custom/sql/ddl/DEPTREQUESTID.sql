DECLARE
   objExists   CHAR (1);
BEGIN
   BEGIN
      SELECT 'Y'
        INTO objExists
        FROM all_objects
       WHERE object_name = 'DEPTREQUESTID';
   EXCEPTION
      WHEN NO_DATA_FOUND
      THEN
         objExists := 'N';
   END;

   IF objExists = 'N'
   THEN
      EXECUTE IMMEDIATE ('
CREATE SEQUENCE DEPTREQUESTID
  START WITH 1
  MAXVALUE 9999999999999999999999999999
  MINVALUE 1
  NOCYCLE
  CACHE 20
  NOORDER');
   ELSE
      DBMS_OUTPUT.put_line (
         'Sequence DEPTREQUESTID already exists');
   END IF;
END;
/