import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioManager{
	private boolean is_jar;
	private JarFile jar;
	private HashMap<String,URL> wav_urls;

	public AudioManager() {
		this(true);
	}

	public AudioManager(boolean is_jar) {
		this.is_jar=is_jar;
		wav_urls=new HashMap<>();
	}

	public AudioManager(boolean is_jar, boolean do_cache_now) throws CacheException {
		this(is_jar);
		if(do_cache_now) cache();
	}

	public static String getJarFilePath() throws FileNotFoundException {
		String path = AudioManager.class.getResource(AudioManager.class.getSimpleName() + ".class").getFile();
		if(path.startsWith("/")) {
			throw new FileNotFoundException("This is not a jar file: \n"+path);
		}
		if(path.lastIndexOf("!")!=-1) path = path.substring(path.lastIndexOf("!/")+2, path.length());
		path = ClassLoader.getSystemClassLoader().getResource(path).getFile();

		return path.substring(0, path.lastIndexOf('!')).replaceAll("%20", " ");
	}

	public void cache() throws CacheException {
		wav_urls.clear();

		if(is_jar) cacheFromJar();
		else			cacheOutsideJar();
	}

	// JAR loader
	private void cacheFromJar() throws CacheException {
		try {
			final String jarstr = new File(getJarFilePath()).toString();
			jar = new JarFile(jarstr.substring(jarstr.indexOf("\\")+1, jarstr.length()));
			final Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
			while(entries.hasMoreElements()) {
				final String name = entries.nextElement().getName();
				if(name.endsWith(".wav")){
					wav_urls.put(name,AudioManager.class.getResource("/"+name));
				}
			}
		}catch(IOException e) {
			wav_urls.clear();
			throw new CacheException("Error while caching audios inside JAR: "+e.getMessage());
		}
	}

	// IDE loader
	private void cacheOutsideJar() throws CacheException{
		try {
			final File apps2 = new File(AudioManager.class.getResource("/").toURI());
			for (File app : apps2.listFiles()) {
				final String name = app.getName();
				if(name.endsWith(".wav")){
					wav_urls.put(name,AudioManager.class.getResource("/"+name));
				}
			}
		}catch(URISyntaxException e) {
			wav_urls.clear();
			throw new CacheException("Error while caching audios outside JAR: "+e.getMessage());
		}
	}

	public void playback(String filename) throws CacheException {
		playback(wav_urls.get(filename));
	}

	public void playback(URL audio_url) throws CacheException {
		if(audio_url==null) throw new NullPointerException("Error: 'audio_url' parameter is null.");

		try {
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(audio_url);
			DataLine.Info info = new DataLine.Info(Clip.class, audioStream.getFormat());
			Clip audioClip = (Clip) AudioSystem.getLine(info);
			audioClip.open(audioStream);
			audioClip.start();
		}catch(IOException | UnsupportedAudioFileException | LineUnavailableException e) {
			throw new CacheException("Error while doing audio playback of file '"+audio_url+"': "+e.getMessage());
		}
	}
}

class CacheException extends Exception {
	private static final long serialVersionUID = 6545985415487000970L;

	public CacheException(String message) {
		super("[AudioManager] "+message);
	}
}
