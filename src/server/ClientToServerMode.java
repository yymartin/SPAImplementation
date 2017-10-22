package server;

public enum ClientToServerMode {
	REGISTER {
		@Override
		public String toString() {
			return "REGISTER";
		}
	},
	
	CHALLENGE {
		@Override
		public String toString() {
			return "CHALLENGE";
		}
	},
	
	AUTH {
		@Override
		public String toString() {
			return "AUTH";
		}
	}
}
