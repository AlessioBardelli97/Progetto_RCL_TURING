import java.util.concurrent.locks.ReentrantLock;

class MulticastArray {

    private MulticastAddress[] group;

    MulticastArray() {

        group = new MulticastAddress[UtilsClass.MAX_MULTICASTADDRESS_NUMBER];

        for (int i = 0; i < UtilsClass.MAX_MULTICASTADDRESS_NUMBER; i++)
            group[i] = new MulticastAddress(i);
    }

    String getAddress() {

        for (MulticastAddress multicastAddress : group) {

            boolean flag = multicastAddress.setInUse();

            if (flag)
                return multicastAddress.getAddress();
        }

        return null;
    }

    void freeAddress(String oldAddress) {

        for (MulticastAddress multicastAddress : group) {

            if (oldAddress.equals(multicastAddress.getAddress())) {
                multicastAddress.freeUse();
            }
        }

    }

    private class MulticastAddress {

        private String address;
        private boolean inUse;
        private ReentrantLock lock_use;

        MulticastAddress(Integer i) {

            this.address = "239.0.0." + i.toString();
            this.inUse = false;
            this.lock_use = new ReentrantLock();
        }

        //restituisce false se l'indirizzo è in uso, altrimenti setta l'indirizzo in uso e restituisce true
        boolean setInUse () {

            lock_use.lock();

            if (inUse) {

                lock_use.unlock();
                return false;
            }

            inUse = true;
            lock_use.unlock();

            return true;
        }

        //restituisce la stringa che codifica l'indirizzo di multicast
        String getAddress() { return this.address; }

        //setta inUse a false
        void freeUse() {

            lock_use.lock();
            inUse = false;
            lock_use.unlock();
        }
    }
}