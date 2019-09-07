/**
 * Alessio Bardelli Mat. 544270
 */

/** Classe utilizzata per gestire gli indirizzi di multicast. */

class MulticastArray {

// ================= VARIABILI DI ISTANZA ==================================================== \\

    /** array di indirizzi di multicast */
    private MulticastAddress[] group;

    @SuppressWarnings("FieldCanBeLocal")
    private int max_address = UtilsClass.MAX_MULTICASTADDRESS_NUMBER;

// ================= COSTRUTTORE ============================================================= \\

    MulticastArray() {

        group = new MulticastAddress[max_address];

        for (int i = 0; i < max_address; i++)
            group[i] = new MulticastAddress(i);
    }

// ================= METODI DI ISTANZA ======================================================= \\

    /**
     * Metodo utilizzato per ottenere il primo indirizzo di multicast libero.
     * @return {@code String} che codifica l'indirizzo di multicast
     */
    String getAddress() {

        for (MulticastAddress multicastAddress : group)
            if (multicastAddress.setInUse())
                return multicastAddress.getAddress();

        return null;
    }

// ================= INNER CLASS ============================================================= \\

    /**
     * Classe che rappresenta gli indirizzi di multicast, utilizzando una stringa,
     * che codifica il vero e proprio indirizzo, e un booleano che e settato a true
     * se l'indirizzo associato è gia in uso, false altrimenti.
     */

    private static class MulticastAddress {

        private String address;
        private boolean inUse;

        MulticastAddress(int i) {

            this.address = "239.0.0." + i;
            this.inUse = false;
        }

        /**
         * Restituisce false se l'indirizzo associato è gia in uso,
         * altrimenti setta l'indirizzo in uso e restituisce true.
         */
        boolean setInUse () {

            if (inUse)
                return false;

            inUse = true;
            return true;
        }

        /** Restituisce la stringa che codifica l'indirizzo di multicast */
        String getAddress() { return this.address; }
    }
}