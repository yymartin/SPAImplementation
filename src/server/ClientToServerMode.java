package server;

/**
 * @author yoanmartin
 * Enumeration representing the different state of a user from the server point of view
 */
public enum ClientToServerMode {
	REGISTERED {
		@Override
		public String toString() {
			return "REGISTERED";
		}
	},
	
	READYTOAUTH {
		@Override
		public String toString() {
			return "READYTOAUTH";
		}
	},
	
	AUTH {
		@Override
		public String toString() {
			return "AUTH";
		}
	}
}
