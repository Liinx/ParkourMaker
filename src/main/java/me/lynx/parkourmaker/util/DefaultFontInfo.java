package me.lynx.parkourmaker.util;

public enum DefaultFontInfo {

    A('A', 5, 7),
    a('a', 5, 6),
    B('B', 5, 7),
    b('b', 5, 6),
    C('C', 5, 7),
    c('c', 5, 6),
    D('D', 5, 7),
    d('d', 5, 6),
    E('E', 5, 7),
    e('e', 5, 6),
    F('F', 5, 7),
    f('f', 4, 5),
    G('G', 5, 7),
    g('g', 5, 6),
    H('H', 5, 7),
    h('h', 5, 6),
    I('I', 3, 4),
    i('i', 1, 2),
    J('J', 5, 7),
    j('j', 5, 6),
    K('K', 5, 7),
    k('k', 4, 5),
    L('L', 5, 7),
    l('l', 1, 2),
    M('M', 5, 7),
    m('m', 5, 6),
    N('N', 5, 7),
    n('n', 5, 6),
    O('O', 5, 7),
    o('o', 5, 6),
    P('P', 5, 7),
    p('p', 5, 6),
    Q('Q', 5, 7),
    q('q', 5, 6),
    R('R', 5, 7),
    r('r', 5, 6),
    S('S', 5, 7),
    s('s', 5, 6),
    T('T', 5, 7),
    t('t', 4, 5),
    U('U', 5, 7),
    u('u', 5, 6),
    V('V', 5, 7),
    v('v', 5, 6),
    W('W', 5, 7),
    w('w', 5, 6),
    X('X', 5, 7),
    x('x', 5, 6),
    Y('Y', 5, 7),
    y('y', 5, 6),
    Z('Z', 5, 7),
    z('z', 5, 6),
    NUM_1('1', 4, 5),
    NUM_2('2', 5, 6),
    NUM_3('3', 5, 6),
    NUM_4('4', 5, 6),
    NUM_5('5', 5, 6),
    NUM_6('6', 5, 6),
    NUM_7('7', 5, 6),
    NUM_8('8', 5, 6),
    NUM_9('9', 5, 6),
    NUM_0('0', 5, 6),
    EXCLAMATION_POINT('!', 1, 2),
    AT_SYMBOL('@', 6,8),
    NUM_SIGN('#', 5,6),
    DOLLAR_SIGN('$', 4,5),
    PERCENT('%', 5,6),
    UP_ARROW('^', 4,5),
    AMPERSAND('&', 5,6),
    ASTERISK('*', 4,5),
    LEFT_PARENTHESIS('(', 4,5),
    RIGHT_PERENTHESIS(')', 4,5),
    MINUS('-', 5,6),
    UNDERSCORE('_', 5,5),
    PLUS_SIGN('+', 5,6),
    EQUALS_SIGN('=', 5,6),
    LEFT_CURL_BRACE('{', 4,5),
    RIGHT_CURL_BRACE('}', 4,5),
    LEFT_BRACKET('[', 3,4),
    RIGHT_BRACKET(']', 3,4),
    COLON(':', 1,2),
    SEMI_COLON(';', 1,2),
    DOUBLE_QUOTE('"', 3,4),
    SINGLE_QUOTE('\'', 1,2),
    LEFT_ARROW('<', 4,5),
    RIGHT_ARROW('>', 4,5),
    QUESTION_MARK('?', 5,6),
    SLASH('/', 5,6),
    BACK_SLASH('\\', 5,6),
    LINE('|', 1,2),
    TILDE('~', 5,6),
    TICK('`', 2,3),
    PERIOD('.', 1,2),
    COMMA(',', 1,2),
    SPACE(' ', 3,3),
    DEFAULT('a', 5,6);

    private final char character;
    private final int length;
    private final int boldLength;

    DefaultFontInfo(char character, int length, int boldLength) {
        this.character = character;
        this.length = length;
        this.boldLength = boldLength;
    }

    public char getCharacter() {
        return this.character;
    }

    public int getLength() {
        return this.length;
    }

    public int getBoldLength() {
        return this.boldLength;
    }

    public static DefaultFontInfo getDefaultFontInfo(char c) {
        for (DefaultFontInfo dFI : DefaultFontInfo.values()) {
            if (dFI.getCharacter() == c) return dFI;
        }
        return DefaultFontInfo.DEFAULT;
    }

}