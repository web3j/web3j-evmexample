pragma solidity ^0.4;

import "./Greeter.sol";

contract Regreeter is Greeter {
    constructor(string _greeting) public Greeter(_greeting) {
    }

    function setGreeting(string _greeting) public {
        emit Modified(greeting, _greeting);
        greeting = _greeting;
    }

    event Modified(
        string indexed oldGreeting, string indexed newGreeting);
}
