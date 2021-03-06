package com.creativemd.creativecore.common.utils.type;

import java.util.HashMap;
import java.util.Map;

public class HashMapFloat<K> extends HashMap<K, Float> {
	
	public HashMapFloat() {
		super();
	}
	
	public HashMapFloat(Map<? extends K, ? extends Float> paramMap) {
		super(paramMap);
	}
	
	@Override
	public Float put(K paramK, Float paramV) {
		Float value = get(paramK);
		if (value != null)
			value += paramV;
		return super.put(paramK, value);
	}
	
	@Override
	public void putAll(Map<? extends K, ? extends Float> paramMap) {
		for (Entry<? extends K, ? extends Float> entry : paramMap.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}
}
