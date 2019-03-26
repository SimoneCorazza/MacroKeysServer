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
	 * @return Path del file delle opzioni
	 */
	public String getPath() {
		return path;
	}
	
	
	/**
	 * Carica dal file le opzioni.
	 * I dati attualmente memorizzati e non salvati verranno persi.
	 * @throws IOException In caso di errore di IO
	 */
	public void load() throws IOException {
		map.clear();
		
		FileInputStream is = new FileInputStream(path);
		DataInputStream di = new DataInputStream(is);
		
		try {
			// Header: numero di entry
			int count = di.readInt();
			
			// Campo dati: coppie (chiavi, dati)
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
	 * Salva nel file le opzioni
	 * @throws IOException In caso di errore di IO
	 */
	public void save() throws IOException {
		FileOutputStream os = new FileOutputStream(path);
		DataOutputStream ds = new DataOutputStream(os);
		
		try {
			// Header: numero di entry
			ds.writeInt(map.size());
			
			// Campo dati: memorizzazione coppie (chiavi, dati)
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
				// Ignoro
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
	 * Inserisce la lista alla chiave associata
	 * @param key Chiave che identifica il dato
	 * @param l Lista da memorizzare
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
	 * Ottiene la lista dalla chiave indicata
	 * @param key Chiave che identifica il dato
	 * @param def Valore di default se la chiave non viene trovata
	 * @return Lista memorizzata
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
	 * Memorizza i dati forniti da {@code saver}
	 * @param key Chiave che identifica il dato
	 * @param item Elemento da memrorizzare
	 * @param saver Interfaccia che memorizza in uno stream {@code item}
	 */
	public <T> void put(String key, T item, PrefSaver<T> saver) {
		if(key == null) {
			throw new NullPointerException();
		}
		
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
				// Niente
			}
		}
	}
	
	
	/**
	 * Ottiene i dati dalla chiave indicata
	 * @param key Chiave che identifica il dato
	 * @param loader Interfaccia che carica dal formato binario i dati
	 * @param def Valore di default se la chiave non viene trovata
	 * @return Dati caricati
	 */
	public <T> T get(String key, PrefLoader<T> loader, T def) {
		if(key == null || loader == null) {
			throw new NullPointerException();
		}
		
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
				// Niente
			}
		}
	}
	
	
	
	/**
	 * Ottiene la lista memorizzata nelle preferenze
	 * @param key Chiave della proprietà
	 * @param loader Permette di caricare gli elementi della lista dallo stream
	 * @param def Valore di default se la chiave non viene trovata
	 * @return Lista di elementi memorizzati o valore di default
	 */
	public <T> List<T> getList(String key, PrefLoader<T> loader,
			List<T> def) {
		if(key == null || loader == null) {
			throw new NullPointerException();
		}
		
		// Caso chiave non presente
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
				// Niente
			}
		}
	}
	
	
	/**
	 * Inserisce la lista indicata nelle opzioni
	 * @param key Chiave della proprietà
	 * @param saver Funzione per salvare gli item
	 * @param data Lista da salvere
	 * @throws NullPointerException Se un parametro è null
	 */
	public <T> void putList(String key, PrefSaver<T> saver, List<T> data) {
		if(saver == null || data == null) {
			throw new NullPointerException();
		}
		
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
				// Niente
			}
		}
	}
}
