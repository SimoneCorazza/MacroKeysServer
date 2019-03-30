package com.macrokeysserver.option;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import java.util.Map.Entry;


public class OptionManager {
	
	private Map<String, byte[]> map = new HashMap<>();
	
	private final String path;
	
	
	public OptionManager(String path) {
		if(path == null) {
			throw new NullPointerException();
		}
		
		this.path = path;
	}
	
	
	
	/**
	 * @return Path of the option file
	 */
	public String getPath() {
		return path;
	}
	
	
	/**
	 * Load from the option file
	 * <p>
	 * Overwrites the actual stored data.
	 * </p>
	 * @throws IOException In case of an IO error
	 */
	public void load() throws IOException {
		map.clear();
		
		FileInputStream is = new FileInputStream(path);
		DataInputStream di = new DataInputStream(is);
		
		try {
			// Header: number of entity
			int count = di.readInt();
			
			// Data body: coples (key, data)
			for(int i = 0; i < count; i++) {
				String key = di.readUTF();
				int length = di.readInt();
				byte[] data = new byte[length];
				di.readFully(data);
				
				map.put(key, data);
			}
		} catch(IOException e) {
			throw e;
		} finally {
			di.close();
		}
	}
	
	
	/**
	 * Save in the option file
	 * @throws IOException In case of an IO error
	 */
	public void save() throws IOException {
		FileOutputStream os = new FileOutputStream(path);
		DataOutputStream ds = new DataOutputStream(os);
		
		try {
			// Header: number of entity
			ds.writeInt(map.size());
			
			// Data body: coples (key, data)
			for(Entry<String, byte[]> e : map.entrySet()) {
				ds.writeUTF(e.getKey());
				ds.writeInt(e.getValue().length);
				ds.write(e.getValue());
			}
		} catch(IOException e) {
			throw e;
		} finally {
			try {
				ds.close();
			} catch(IOException e) {
				// Ignore
			}
		}
		
	}
	
	
	public void put(String key, int i) {
		final PrefSaver<Integer> l = new PrefSaver<Integer>() {
			public void save(Integer item, DataOutputStream str)
					throws IOException {
				str.writeInt(item);
			}
		};
		
		put(key, i, l);
	}
	
	
	
	public int get(String key, int def) {
		final PrefLoader<Integer> l = new PrefLoader<Integer>() {
			public Integer load(DataInputStream str) throws IOException {
				return str.readInt();
			}
		};
		
		return get(key, l, def);
	}
	
	
	/**
	 * Insert the list at the associated key
	 * @param key Key of the data
	 * @param l List to store
	 */
	public void put(String key, List<String> l) {
		final PrefSaver<String> s = new PrefSaver<String>() {
			public void save(String item, DataOutputStream str) throws IOException {
				str.writeUTF(item);
			}
		};
		
		putList(key, s, l);
	}
	
	
	/**
	 * Gets the list of the given key
	 * @param key Key of the list to get
	 * @param def Default value if the key is not found
	 * @return Stored list or {@code def}
	 */
	public List<String> get(String key, List<String> def) {
		final PrefLoader<String> l = new PrefLoader<String>() {
			public String load(DataInputStream str) throws IOException {
				return str.readUTF();
			}
		};
		
		return getList(key, l, def);
	}
	
	
	
	/**
	 * Store the datata given by {@code saver}
	 * @param key Key of the data to store
	 * @param item Item to store
	 * @param saver Object used to save the {@code item}
	 */
	public <T> void put(@NonNull String key, T item, PrefSaver<T> saver) {
		Objects.requireNonNull(key);
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		DataOutputStream ds = new DataOutputStream(os);
		
		try {
			saver.save(item, ds);
			
			byte[] data = os.toByteArray();
			map.put(key, data);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				os.close();
			} catch (IOException e) {
				// Nothing
			}
		}
	}
	
	
	
	/**
	 * Gets the data associated with the given key
	 * @param key Key that identifies the data
	 * @param loader Loader that load the data
	 * @param def Default value if the key is not found
	 * @return Loaded data or the default data
	 */
	public <T> T get(@NonNull String key, @NonNull PrefLoader<T> loader, T def) {
		Objects.requireNonNull(key);
		Objects.requireNonNull(loader);
		
		if(!map.containsKey(key)) {
			return def;
		}
		
		byte[] data = map.get(key);
		ByteArrayInputStream is = new ByteArrayInputStream(data);
		DataInputStream di = new DataInputStream(is);
		
		try {
			return loader.load(di);
		} catch(IOException e) {
			e.printStackTrace();
			
			return def;
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				// Nothing
			}
		}
	}
	
	
	
	/**
	 * Gests the list stored in preferences
	 * @param key Key of the property
	 * @param loader Loads the items of the list of the stream
	 * @param def Default value if the key is not found
	 * @return List of stored items or default value {@code def} 
	 */
	public <T> List<T> getList(String key, PrefLoader<T> loader,
			List<T> def) {
		if(key == null || loader == null) {
			throw new NullPointerException();
		}
		
		if(!map.containsKey(key)) {
			return def;
		}
		
		byte[] data = map.get(key);

		List<T> l = new ArrayList<>();
		
		ByteArrayInputStream is = new ByteArrayInputStream(data);
		try {
			DataInputStream str = new DataInputStream(is);
			int size = str.readInt();
			for(int i = 0; i < size; i++) {
				T loaded = loader.load(str);
				l.add(loaded);
			}
			
			return l;
		} catch(Exception e) {
			e.printStackTrace();
			return def;
		} finally {
			try {
				is.close();
			} catch(IOException e) {
				// Nothing
			}
		}
	}
	
	
	/**
	 * Insert the given list in the options
	 * @param key Key of the property
	 * @param saver Function to save the items
	 * @param data List to save
	 */
	public <T> void putList(@NonNull String key, @NonNull PrefSaver<T> saver,
			@NonNull List<T> data) {
		Objects.requireNonNull(key);
		Objects.requireNonNull(saver);
		Objects.requireNonNull(data);
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		DataOutputStream str = new DataOutputStream(os);
		
		try {
			str.writeInt(data.size());
			for(T t : data) {
				saver.save(t, str);
			}
			
			map.put(key, os.toByteArray());
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				os.close();
			} catch(IOException e) {
				// Nothing
			}
		}
	}
}
