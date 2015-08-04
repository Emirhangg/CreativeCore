package com.n247s.api.eventapi;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.logging.log4j.Logger;

public abstract class CallHandler
{
	private static final Logger log = EventApi.logger;
	
	public final List<Object> entryCheckList = new ArrayList<Object>();
	protected final HashMap<CustomEventSubscribe.Priority, LinkedHashMap> instanceMap = new HashMap<CustomEventSubscribe.Priority, LinkedHashMap>();
	protected final Class<? extends EventType> eventType;
	
	public CallHandler(Class<? extends EventType> eventType)
	{
		this.eventType = eventType;
		instanceMap.put(CustomEventSubscribe.Priority.Highest, new LinkedHashMap());
		instanceMap.put(CustomEventSubscribe.Priority.High, new LinkedHashMap());
		instanceMap.put(CustomEventSubscribe.Priority.Normal, new LinkedHashMap());
		instanceMap.put(CustomEventSubscribe.Priority.Low, new LinkedHashMap());
		instanceMap.put(CustomEventSubscribe.Priority.Lowest, new LinkedHashMap());
	}

	/** Used to raise the bound event
	 * @return */
	protected abstract boolean CallInstances();
	
	/**
	 * This method is used to add an instance of a Class which should be called on eventRaise.
	 * Its called from {@link EventApi#RegisterEventClassInstance(Object)}.
	 * 
	 * @param priority
	 * @param classInstance
	 * @param method
	 */
	protected final void RegisterEventListner(CustomEventSubscribe.Priority priority, Object Listner, Method method)
	{
		this.instanceMap.get(priority).put(Listner, method);
		this.entryCheckList.add(Listner);
	}
	
	/**
	 * This method is used to remove an EventListner (Class object / Class instance).
	 * Its called from {@link EventApi#removeEventListner(Object)}.
	 * 
	 * @param Listner
	 */
	protected final void removeListner(CustomEventSubscribe.Priority priority, Object Listner)
	{
		LinkedHashMap currenInstanceMap = this.instanceMap.get(priority);
		
		if(currenInstanceMap == null)
			log.catching(new NullPointerException("There is no Listner registered for this priority level " + priority.toString() + "!"));
		
		if(currenInstanceMap.containsKey(Listner))
			currenInstanceMap.remove(Listner);
		else log.catching(new NullPointerException("Current Listner has either changed its annotation value's or is not registered!"));
	}
}
