package gamezone.common;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

/**
 * Thin wrapper around the Twilio WhatsApp API.
 *
 * Required environment variables:
 *   TWILIO_ACCOUNT_SID   – Twilio Account SID
 *   TWILIO_AUTH_TOKEN    – Twilio Auth Token
 *   TWILIO_WHATSAPP_FROM – Sender number in E.164, e.g. +14155238886
 *                          (use the sandbox number while testing)
 */
public final class WhatsAppService {

    private static final WhatsAppService INSTANCE = new WhatsAppService();

    private final boolean enabled;
    private final String from;

    private WhatsAppService() {
        String sid   = System.getenv("TWILIO_ACCOUNT_SID");
        String token = System.getenv("TWILIO_AUTH_TOKEN");
        from         = System.getenv("TWILIO_WHATSAPP_FROM");

        if (sid != null && !sid.isBlank() && token != null && !token.isBlank()) {
            Twilio.init(sid, token);
            enabled = true;
        } else {
            enabled = false;
        }
    }

    public static WhatsAppService getInstance() {
        return INSTANCE;
    }

    /**
     * Sends a WhatsApp message. Falls back to stdout when env vars are not set.
     */
    public void sendOtp(String toPhone, String code) {
        if (!enabled) {
            System.out.println("[DEMO] WhatsApp OTP for " + toPhone + " → " + code);
            return;
        }

        Message.creator(
                new PhoneNumber("whatsapp:" + toPhone),
                new PhoneNumber("whatsapp:" + from),
                "Your Gamezone verification code is: *" + code + "*\nIt expires in 10 minutes."
        ).create();
    }
}
