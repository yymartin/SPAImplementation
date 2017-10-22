package storage;

public enum ClientToStorageMode {
	STORE {
		@Override
	    public String toString() {
		      return "STORE";
		    }
	}, RETRIEVE {
		@Override
	    public String toString() {
		      return "RETRIEVE";
		    }
	};
}
