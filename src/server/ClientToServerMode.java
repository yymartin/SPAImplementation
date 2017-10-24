package server;

/**
 * @author yoanmartin
 * Enumeration representing the different state of a user from the server point of view
 */
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
