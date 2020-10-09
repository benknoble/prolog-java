/*
BSD License

Copyright (c) 2013, Tom Everett
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.
3. Neither the name of Tom Everett nor the names of its contributors
   may be used to endorse or promote products derived from this software
   without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

grammar Prolog;

@lexer::members {
public static final int COMMENTCH = 1;
}

// Prolog text and data formed from terms (6.2)

p_text: (directive | clause) * EOF ;

directive: ':-' term '.' ; // also 3.58

clause
    : term ':-' term '.' # predicate
    | term '.' # fact
    ; // also 3.33


// Abstract Syntax (6.3): terms formed from tokens

termlist
    : term ( ',' term )*
    ;

term: binary1200;

// 1200  xfx  -->, :-
binary1200
    : unary1200 (':-' | '-->') unary1200
    | unary1200
    ;

// 1200  fx   :-, ?-
unary1200
    : <assoc=right> (':-' | '?-') unary1150
    | unary1150
    ;

/* 1150  fx
 * dynamic, discontiguous, initialization, meta_predicate, module_transparent,
 * multifile, public, thread_local, thread_initialization, volatile
 */
unary1150
    : (
            'dynamic'
            | 'discontiguous'
            | 'initialization'
            | 'meta_predicate'
            | 'module_transparent'
            | 'multifile'
            | 'public'
            | 'thread_local'
            | 'thread_initialization'
            | 'volatile'
    ) binaryRight1100
    | binaryRight1100
    ;

// 1100  xfy  ;, |
binaryRight1100
    : <assoc=right> binaryRight1050 ((';' | '|') binaryRight1100)+
    | binaryRight1050
    ;

// 1050  xfy  ->, *->
binaryRight1050
    : <assoc=right> binaryRight1000 ('->' | '*->') binaryRight1050
    | binaryRight1000
    ;

// 1000  xfy  ,
binaryRight1000
    : <assoc=right> binary990 (',' binaryRight1000)+
    | binary990
    ;

// 990  xfx  :=
binary990
    : unary900 ':=' unary900
    | unary900
    ;

// 900  fy   \+
unary900
    : <assoc=right> '\\+' unary900
    | binary700
    ;

/* 700  xfx  <, =, =.., =@=, \=@=, =:=, =<, ==, =\=, >, >=, @<, @=<, @>, @>=, \=, \==, as,
 * is, >:<, :<
 */
binary700
    : binaryRight600 (
            '<'
            | '='
            | '=..'
            | '=@='
            | '\\=@='
            | '=:='
            | '=<'
            | '=='
            | '=\\='
            | '>'
            | '>='
            | '@<'
            | '@=<'
            | '@>'
            | '@>='
            | '\\='
            | '\\=='
            | 'as'
            | 'is'
            | '>:<'
            | ':<'
            ) binaryRight600
    | binaryRight600
    ;

// 600  xfy  :
binaryRight600
    : <assoc=right> binaryLeft500 ':' binaryRight600
    | binaryLeft500
    ;

// 500  yfx  +, -, /\, \/, xor
binaryLeft500
    : <assoc=left> binaryLeft500 ('+' | '-' | '/\\' | '\\/' | 'xor') unary500
    | unary500
    ;

// 500  fx   ?
unary500
    : '?' binaryLeft400
    | binaryLeft400
    ;

// 400  yfx  *, /, //, div, rdiv, <<, >>, mod, rem
binaryLeft400
    : <assoc=left> binaryLeft400 (
            '*'
            | '/'
            | '//'
            | 'div'
            | 'rdiv'
            | '<<'
            | '>>'
            | 'rem'
            | 'mod'
            ) binary200
    | binary200
    ;

// 200  xfx  **
binary200
    : <assoc=right> binaryRight200 '**' binaryRight200
    | binaryRight200
    ;

// 200  xfy  ^
binaryRight200
    : <assoc=right> unary200 '^' binaryRight200
    | unary200
    ;

// 200  fy   +, -, \
unary200
    : <assoc=right> ('+' | '-' | '\\') unary200
    | unary1
    ;

// 1  fx   $
unary1
    : '$' base_term
    | base_term
    ;

base_term
    : VARIABLE          # variable
    | '(' term ')'      # braced_term
    | integer      # integer_term
    | FLOAT        # float
    // structure / compound term
    | atom '(' termlist ')'     # compound_term
    | '[' termlist ']' # list_term
    | '{' termlist '}'          # curly_bracketed_term
    // atom
    | atom              # atom_term
    ;

atom // 6.4.2 and 6.1.2
    : '[' ']'           # empty_list //NOTE [] is not atom anymore in swipl 7 and later
    | '{' '}'           # empty_braces
    | LETTER_DIGIT      # name
    | GRAPHIC_TOKEN     # graphic
    | QUOTED            # quoted_string
    | DOUBLE_QUOTED_LIST# dq_string
    | BACK_QUOTED_STRING# backq_string
    | ';'               # semicolon
    | '!'               # cut
    ;


integer // 6.4.4
    : DECIMAL
    | CHARACTER_CODE_CONSTANT
    | BINARY
    | OCTAL
    | HEX
    ;


// Lexer (6.4 & 6.5): Tokens formed from Characters

LETTER_DIGIT // 6.4.2
    : SMALL_LETTER ALPHANUMERIC*
    ;

VARIABLE // 6.4.3
    : CAPITAL_LETTER ALPHANUMERIC*
    | '_' ALPHANUMERIC+
    | '_'
    ;

// 6.4.4
DECIMAL: DIGIT+ ;
BINARY: '0b' [01]+ ;
OCTAL: '0o' [0-7]+ ;
HEX: '0x' HEX_DIGIT+ ;

CHARACTER_CODE_CONSTANT: '0' '\'' SINGLE_QUOTED_CHARACTER ;

FLOAT: DECIMAL '.' [0-9]+ ( [eE] [+-] DECIMAL )? ;


GRAPHIC_TOKEN: (GRAPHIC | '\\')+ ; // 6.4.2
fragment GRAPHIC: [#$&*+./:<=>?@^~] | '-' ; // 6.5.1 graphic char

// 6.4.2.1
fragment SINGLE_QUOTED_CHARACTER: NON_QUOTE_CHAR | '\'\'' | '"' | '`' ;
fragment DOUBLE_QUOTED_CHARACTER: NON_QUOTE_CHAR | '\'' | '""' | '`' ;
fragment BACK_QUOTED_CHARACTER: NON_QUOTE_CHAR | '\'' | '"' | '``' ;
fragment NON_QUOTE_CHAR
    : GRAPHIC
    | ALPHANUMERIC
    | SOLO
    | ' ' // space char
    | META_ESCAPE
    | CONTROL_ESCAPE
    | OCTAL_ESCAPE
    | HEX_ESCAPE
    ;
fragment META_ESCAPE: '\\' [\\'"`] ; // meta char
fragment CONTROL_ESCAPE: '\\' [abrftnv] ;
fragment OCTAL_ESCAPE: '\\' [0-7]+ '\\' ;
fragment HEX_ESCAPE: '\\x' HEX_DIGIT+ '\\' ;

QUOTED:          '\'' (CONTINUATION_ESCAPE | SINGLE_QUOTED_CHARACTER )*? '\'' ; // 6.4.2
DOUBLE_QUOTED_LIST: '"' (CONTINUATION_ESCAPE | DOUBLE_QUOTED_CHARACTER )*? '"'; // 6.4.6
BACK_QUOTED_STRING: '`' (CONTINUATION_ESCAPE | BACK_QUOTED_CHARACTER )*? '`'; // 6.4.7
fragment CONTINUATION_ESCAPE: '\\\n' ;

// 6.5.2
fragment ALPHANUMERIC: ALPHA | DIGIT ;
fragment ALPHA: '_' | SMALL_LETTER | CAPITAL_LETTER ;
fragment SMALL_LETTER: [a-z_];

fragment CAPITAL_LETTER: [A-Z];

fragment DIGIT: [0-9] ;
fragment HEX_DIGIT: [0-9a-fA-F] ;

// 6.5.3
fragment SOLO: [!(),;[{}|%] | ']' ;


WS
   : [ \t\r\n]+ -> skip
   ;

// see COMMENTCH
COMMENT: '%' ~[\n\r]* ( [\n\r] | EOF) -> channel(1) ;
MULTILINE_COMMENT: '/*' ( MULTILINE_COMMENT | . )*? ('*/' | EOF) -> channel(1);
