package foo.bar;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HelloWorld {

	public static void main(String[] args) throws IOException {
		String path = System.getProperty("user.dir");

		Files.write(
			Paths.get(path, "liferay.routes.client.extension"),
			_getEnv("LIFERAY_ROUTES_CLIENT_EXTENSION"));
		Files.write(
			Paths.get(path, "liferay.routes.dxp"),
			_getEnv("LIFERAY_ROUTES_DXP"));

		System.out.println("JAVA JAVA Working Directory = " + path);
	}

	private static byte[] _getEnv(String key) {
		String env = System.getenv(key);

		if (env == null) {
			env = "not found";
		}

		return env.getBytes();
	}

}