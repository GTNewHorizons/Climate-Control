
package climateControl.utils;

/**
 *
 * @author Zeno410
 */
public interface Mutable<Type> extends Trackable<Type> {

    public void set(Type newValue);

    public void set(Mutable<Type> toCopy);

    public Type value();

    public class Concrete<CType> implements Mutable<CType> {

        private CType type;
        private Trackers<CType> trackers = new Trackers<CType>();

        public Concrete(CType initial) {
            type = initial;
        }

        public void set(CType newValue) {
            if ((type == null && newValue != null) || (!type.equals(value()))) {
                type = newValue;
                trackers.update(newValue);
            }
        }

        public void set(Mutable<CType> toCopy) {
            set(toCopy.value());
        }

        public CType value() {
            return type;
        }

        public void informOnChange(Acceptor<CType> target) {
            trackers.informOnChange(target);
        }

        public void stopInforming(Acceptor<CType> target) {
            trackers.stopInforming(target);
        }

    }
}
