import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;

public class SuperAnimal implements Animal {
	private Animal _animal;
	private Class<? extends Feature> _featureClz;

	public SuperAnimal(Animal a, Class<? extends Feature> featureClz) {
		this._animal = a;
		this._featureClz = featureClz;
	}

	@Override
	public void doStuff() {
		InvocationHandler handler = new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				Object obj = null;
				if (Modifier.isPublic(method.getModifiers())) {
					obj = method.invoke(_featureClz.newInstance(), args);
				}
				_animal.doStuff();

				return obj;
			}
		};
		
		ClassLoader cl = getClass().getClassLoader();
		// Dynamic Proxy, let handler decides how to decorate
		Feature proxy = (Feature) Proxy.newProxyInstance(cl, _featureClz.getInterfaces(), handler);
		proxy.loadNewFeature();
	}
	
	public static void main(String[] args)
	{
		Animal jerry = new Rat();
		
		jerry = new SuperAnimal(jerry, DigFeature.class);//pass in jerry with nothing, returned jerry with DigFeature.
		jerry = new SuperAnimal(jerry, FlyFeature.class);//pass in jerry with DigFeature. returned jerry with FlyFeature.
		jerry.doStuff();
	}

}
