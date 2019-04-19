package com.generalbytes.batm.server.extensions.extra.snowgem;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.*;
import com.generalbytes.batm.server.extensions.extra.xuez.wallets.xuezd.XuezRPCWallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class XuezExtension extends AbstractExtension {

    private static final Logger LOG = LoggerFactory.getLogger(XuezExtension.class);

    @Override
    public String getName() {
        return "BATM XUEZ extra extension";
    }

    @Override
    public IWallet createWallet(String walletLogin) {
        if (walletLogin != null && !walletLogin.trim().isEmpty()) {
            //"walletname:protocol:user:password:ip:port"
            StringTokenizer st = new StringTokenizer(walletLogin, ":");
            String walletName = st.nextToken();
            if ("xuezd".equals(walletName)){
                String protocol = st.nextToken();
                String username = st.nextToken();
                String password = st.nextToken();
                String hostname = st.nextToken();
                String port = st.nextToken();

                if (protocol != null && username != null && password != null && hostname != null && port != null) {
                    String rpcURL = protocol + "://" + username + ":" + password + "@" + hostname + ":" + port;
                    return new XuezRPCWallet(rpcURL);
                }
            }

            if ("xuezdemo".equalsIgnoreCase(walletName)){
                String fiatCurrency = st.nextToken();
                String walletAddress = "";
                if (st.hasMoreTokens()) {
                    walletAddress = st.nextToken();
                }

                if (fiatCurrency != null && walletAddress != null) {
                    return new DummyExchangeAndWalletAndSource(fiatCurrency, CryptoCurrency.XUEZ.getCode(), walletAddress);
                }
            }
        }

        return null;
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (CryptoCurrency.XUEZ.getCode().equalsIgnoreCase(cryptoCurrency)){
            return new XuezAddressValidator();
        }
        return null;
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {
        if (sourceLogin != null && !sourceLogin.trim().isEmpty()){
            StringTokenizer st = new StringTokenizer(sourceLogin, ":");
            String exchangeType = st.nextToken();
            String preferredFiatCurrency = FiatCurrency.USD.getCode();
            if("xuezfix".equalsIgnoreCase(exchangeType)){
                BigDecimal rate = BigDecimal.ZERO;
                if (st.hasMoreTokens()) {
                    try {
                        rate = new BigDecimal(st.nextToken());
                    } catch (Throwable e) {
                    }
                }
                if (st.hasMoreTokens()) {
                    preferredFiatCurrency = st.nextToken().toUpperCase();
                }
                return new FixPriceRateSource(rate, preferredFiatCurrency);
            }
        }
        return null;
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        Set<String> result = new HashSet<>();
        result.add(CryptoCurrency.XUEZ.getCode());
        return result;
    }
}
