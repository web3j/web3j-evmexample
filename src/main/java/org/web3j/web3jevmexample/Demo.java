/*
 * Copyright 2019 Web3 Labs Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.web3j.web3jevmexample;

import org.hyperledger.besu.ethereum.vm.OperationTracer;
import org.web3j.abi.datatypes.Address;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.evm.Configuration;
import org.web3j.evm.ConsoleDebugTracer;
import org.web3j.evm.PassthroughTracer;
import org.web3j.evm.EmbeddedWeb3jService;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.regreeter.Regreeter;
import org.web3j.tx.Transfer;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;

import java.math.BigDecimal;

public class Demo {
    public static void main(String... args) throws Exception {
        Credentials credentials =
                WalletUtils.loadCredentials("Password123", "resources/demo-wallet.json");

        // Define our own address and how much ether to prefund this address with
        Configuration configuration = new Configuration(new Address(credentials.getAddress()), 10);

        // When using the default constructor on ConsoleDebugTracer, it will look for
        // contract meta data within "build/resources/main/solidity". This is where
        // the Web3j gradle plugin will place these files, but you can pass in a different
        // location if you have placed them elsewhere.
        OperationTracer operationTracer = new ConsoleDebugTracer();

        // If you don't want console debugging, use PassthroughTracer instead..
        //OperationTracer operationTracer = new PassthroughTracer();

        // We use EmbeddedWeb3jService rather than the usual service implementation.
        // This will let us run an EVM and a ledger inside the running JVM..
        Web3j web3j = Web3j.build(new EmbeddedWeb3jService(configuration, operationTracer));

        // First run a simple ETH transfer transaction..
        System.out.println("Starting simple ETH transfer transaction");
        TransactionReceipt transactionReceipt =
                Transfer.sendFunds(
                        web3j,
                        credentials,
                        "0x2dfBf35bb7c3c0A466A6C48BEBf3eF7576d3C420",
                        new BigDecimal("1"),
                        Convert.Unit.ETHER)
                        .send();

        System.out.println(
                "Transfer transaction receipt: " + transactionReceipt.getTransactionHash());
        if (!transactionReceipt.isStatusOK()) throw new RuntimeException("Failed transaction");

        System.out.println();

        // Deploy Greeter contract..
        System.out.println("Starting Greeter deploy..");
        Regreeter regreeter =
                Regreeter.deploy(web3j, credentials, new DefaultGasProvider(), "Hello!").send();

        System.out.println();

        // Fetch greeter value..
        System.out.println("Greeter was deployed, about to get greeting..");

        String greet = regreeter.getGreeting().send();
        System.out.println("Greeter string value is: " + greet);
    }
}
