grammar dsl;

/*
domain "cars"
concept "top" = "blue", "green", "red"
concept "body" = "blue", "green", "red"

solution top = blue, body = blue

*/

program
   : 'domain' name
     concept+
     solution*
   ;

solution
   : ('thing'|'solution') key_value_seq+
   ;
      
key_value_seq
   : key_value (',' key_value)*
   ;

key_value
   : name '=' name
   ;

concept
   : 'concept' name '=' value_seq
   ;
   
value_seq
   : name (',' name)*
   ;
   
name:
   string | ID
   ;

string
   : STRING_LITERAL
   ;

ID
   : VALID_ID_START VALID_ID_CHAR*
   ;

fragment VALID_ID_START
   : 'a' .. 'z' | 'A' .. 'Z' | '_'
   ;

fragment VALID_ID_CHAR
   : VALID_ID_START | '0' .. '9'
   ;

STRING_LITERAL
      : '"' ( ~'"' )* '"'
      ;


WS
   : [ \r\n\t] + -> skip
   ;