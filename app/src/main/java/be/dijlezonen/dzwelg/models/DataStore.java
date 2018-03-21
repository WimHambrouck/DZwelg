package be.dijlezonen.dzwelg.models;

public final class DataStore {

    private DataStore() {}

    private static Lid kassaverantwoordelijke;

    public static DataStore getInstance() {
        return new DataStore();
    }

    public static void setKassaverantwoordelijke(Lid kassaverantwoordelijke) {
        DataStore.kassaverantwoordelijke = kassaverantwoordelijke;
    }

    public static Lid getKassaverantwoordelijke() {
        return kassaverantwoordelijke;
    }
}
