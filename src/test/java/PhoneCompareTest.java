import android.telephony.PhoneNumberUtils;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class PhoneCompareTest {
    @Test
    public void comparesPhones() {
        Assert.assertTrue(PhoneNumberUtils.compare("89059916511", "89059916511"));
    }
}


