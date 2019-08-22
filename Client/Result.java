import java.io.Serializable;

/**
 * Classe che implementa il formato delle risposte che il server manda al client in seguito ad una richiesta.
 * Le risposte predefinite sono:
 *  - la costante 'result//__//ok' per indicaare che l'operazion richiesta è andata a buon fine
 *  - la costante 'result//__//fail' per indicare che l'operazione richiesta non è andata a buon fine
 */

@SuppressWarnings("WeakerAccess")

public class Result implements Serializable {

// ================= VARIABILI STATICHE ===================================================== \\

    /** Costanti per indicare l'esito di operazioni. */
    private static final String OK = "result//__//ok";
    private static final String FAIL = "result//__//fail";

    private static final long serialVersionUID = 1;

// ================= VARIABILI DI ISTANZA =================================================== \\

    /** Stringa che contiene l'informazione del risultato. */
    private String info;

// ================= COSTRUTTORE ============================================================ \\

    Result(String info) { this.info = info; }

    Result (byte[] info) { this.info = new String(info); }

// ================= METODI DI ISTANZA ====================================================== \\

    /** Restituisce un array che contiene le informazioni del risultato. */
    String[] getInfoSplitted() { return this.info.split("//__//"); }

    /** Restituisce la stringa associata al risultato. */
    String getInfo() { return this.info; }

    /**
     * @return {@code true} se il risultato è la costante "result//__//ok",
     * {@code false} altrimenti.
     */
    boolean isOK() { return this.info.equals(Result.OK); }

    /**
     * @return {@code true} se il risultato è la costante "result//__//fail",
     * {@code false} altrimenti.
     */
    boolean isFAIL() { return this.info.equals(Result.FAIL); }

    Request toRequest() { return new Request(this.info); }

// ================= METODI STATICI ========================================================= \\

    /** Restituisce un risultato con la costante "result//__//ok". */
    static Result OK() { return new Result(Result.OK); }

    /** Restituisce un risultato con la costante "result//__//fail". */
    static Result FAIL() { return new Result(Result.FAIL); }
}
