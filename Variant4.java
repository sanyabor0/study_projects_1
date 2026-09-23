import java.io.IOException;

/** Практическая работа 1, вариант 4. Табличные ДКА и НКА. */
public final class Variant4 {
    // Столбцы: a, b. Номер строки = номер состояния.
    static final int[][] DFA = {{1, 0}, {2, 1}, {0, 2}};
    static final boolean[] DFA_FINAL = {false, false, true};

    // NFA[from][symbol][to] = наличие перехода.
    static final boolean[][][] NFA = new boolean[5][2][5];
    static final boolean[] NFA_FINAL = {false, false, true, false, true};
    static {
        NFA[0][0][1] = true;
        NFA[1][1][2] = true;
        NFA[1][1][3] = true; // Недетерминированное ветвление по b.
        NFA[2][0][2] = true; // ab a*
        NFA[3][0][4] = true;
        NFA[4][1][4] = true; // aba b*
    }

    static int symbolIndex(int code) {
        if (code == 'a') return 0;
        if (code == 'b') return 1;
        return -1;
    }

    static int stepDfa(int state, int symbol) {
        if (state < 0 || symbol < 0) return -1;
        return DFA[state][symbol];
    }

    static boolean[] stepNfa(boolean[] current, int symbol) {
        boolean[] next = new boolean[5];
        if (symbol < 0) return next;
        for (int from = 0; from < 5; from++) {
            if (current[from]) {
                for (int to = 0; to < 5; to++) {
                    if (NFA[from][symbol][to]) next[to] = true;
                }
            }
        }
        return next;
    }

    static boolean acceptNfa(boolean[] states) {
        for (int i = 0; i < 5; i++) {
            if (states[i] && NFA_FINAL[i]) return true;
        }
        return false;
    }

    private static void printStates(boolean[] states) {
        System.out.print('{');
        boolean first = true;
        for (int i = 0; i < states.length; i++) {
            if (states[i]) {
                if (!first) System.out.print(", ");
                System.out.print('q');
                System.out.print(i);
                first = false;
            }
        }
        System.out.print('}');
    }

    private static void printStep(int step, int symbol, int dfa, boolean[] nfa) {
        System.out.print(step);
        System.out.print("\t");
        if (step == 0) System.out.print('-');
        else System.out.print((char) symbol);
        System.out.print("\t");
        if (dfa < 0) System.out.print("{}");
        else { System.out.print('q'); System.out.print(dfa); }
        System.out.print("\t");
        printStates(nfa);
        System.out.println();
    }

    private static void printResult(int dfa, boolean[] nfa, boolean valid) {
        System.out.print("DFA: ");
        System.out.println(valid && dfa >= 0 && DFA_FINAL[dfa] ? "Accept" : "Reject");
        System.out.print("NFA: ");
        System.out.println(valid && acceptNfa(nfa) ? "Accept" : "Reject");
        if (!valid) System.out.println("Invalid symbol: alphabet is {a, b}.");
        System.out.println();
    }

    /** Каждая строка ввода - отдельная цепочка; пустая строка задаёт epsilon.
     *  Ввод посимвольный. Методы обработки строк не используются.
     *  CR, LF и CRLF поддерживаются; EOF завершает последнюю непустую строку.
     */
    public static void main(String[] args) throws IOException {
        System.out.println("Variant 4 | DFA and NFA | alphabet {a, b}");
        System.out.println("One input per line. Empty line = epsilon. EOF = exit.");
        int dfa = 0, step = 0;
        boolean[] nfa = {true, false, false, false, false};
        boolean valid = true, started = false, afterCR = false;
        int code;
        while ((code = System.in.read()) != -1) {
            if (afterCR && code == '\n') { afterCR = false; continue; }
            afterCR = false;
            if (!started) {
                System.out.println("Step\tRead\tDFA\tNFA");
                printStep(0, '-', dfa, nfa);
                started = true;
            }
            if (code == '\r' || code == '\n') {
                printResult(dfa, nfa, valid);
                dfa = 0; step = 0;
                nfa = new boolean[] {true, false, false, false, false};
                valid = true; started = false;
                afterCR = code == '\r';
                continue;
            }
            int symbol = symbolIndex(code);
            if (symbol < 0) valid = false;
            dfa = stepDfa(dfa, symbol);
            nfa = stepNfa(nfa, symbol);
            printStep(++step, code, dfa, nfa);
        }
        if (started) printResult(dfa, nfa, valid);
    }
}
