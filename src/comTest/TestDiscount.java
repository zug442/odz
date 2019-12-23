package comTest;

import com.company.ShoppingCart;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.ArrayList;
import java.util.Collection;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class TestDiscount {
    private static final int[] Q_VALUES = {1, 9, 10, 19};
    private static final ShoppingCart.ItemType[] T_VALUES = {ShoppingCart.ItemType.NEW, ShoppingCart.ItemType.REGULAR, ShoppingCart.ItemType.SECOND_FREE, ShoppingCart.ItemType.SALE};
    private static final int[][] D_VALUES = {{0}, {0,0,1}, {0,50,51,51}, {70,70,71,71}};

    @Parameterized.Parameters
    @SuppressWarnings("unchecked")
    public static Collection getTypeQuantityPairs() {
        Collection pairs = new ArrayList();
        for (int q = 0; q < Q_VALUES.length; q++)
            for (int t = 0; t < T_VALUES.length; t++)
                pairs.add(new Object[]{T_VALUES[t], Q_VALUES[q], (D_VALUES[t].length > q) ? D_VALUES[t][q]
                        : D_VALUES[t][D_VALUES[t].length - 1]});
        return pairs;
    }

    private ShoppingCart.ItemType _type;
    private int _quantity;
    private int _discount;

    public TestDiscount(ShoppingCart.ItemType type, int quantity, int discount) {
        _type = type;
        _quantity = quantity;
        _discount = discount;
    }

    @Test
    public void discountTest() {
        assertEquals("type: " + _type + ", quantity: " + _quantity, _discount,
                ShoppingCart.calculateDiscount(_type,  _quantity));
    }
}
