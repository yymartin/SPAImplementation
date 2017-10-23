package SSLUtility;

public enum ProtocolMode {
	SERVER_OPTIMAL {
		@Override
		public String toString() {
			return "SERVER_OPTIMAL";
		}
	},
	
	STORAGE_OPTIMAL {
		@Override
		public String toString() {
			return "STORAGE_OPTIMAL";
		}
	},
	
	PRIVACY_OPTIMAL {
		@Override
		public String toString() {
			return "PRIVACY_OPTIMAL";
		}
	}
}