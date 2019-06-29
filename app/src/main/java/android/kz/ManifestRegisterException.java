package android.kz;

final class ManifestRegisterException extends RuntimeException {

    ManifestRegisterException(String permission) {
        super(permission == null ?
			  "No permissions are registered in the manifest file" :
			  (permission + ": Permissions are not registered in the manifest file"));
    }
}
