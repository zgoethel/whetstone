package net.jibini.whetstone.proxy

import com.beust.klaxon.Klaxon
import org.junit.Assert
import org.junit.Test

interface TestString
{
    var value: String

    companion object
    {
        fun create(value: String): TestString = TestStringImpl(value)
    }
}

class TestStringImpl(
    override var value: String
) : TestString

interface TestStringCarrier
{
    var testString: TestString

    companion object
    {
        fun create(testString: TestString): TestStringCarrier = TestStringCarrierImpl(testString)
    }
}

class TestStringCarrierImpl(
    override var testString: TestString
) : TestStringCarrier

class TestObjectProxying
{
    @Test
    fun proxyObjectUnchanging()
    {
        val base = TestString.create("Hello, world!")
        val proxy = Proxy.create(base)

        Assert.assertEquals("Hello, world!", proxy.value)

        proxy.value = "Foo Bar"

        Assert.assertEquals("Foo Bar", proxy.value)
    }

    @Test
    fun proxyObjectProvider()
    {
        val sources = listOf(TestStringImpl("Hello, world!"), TestStringImpl("Foo Bar"))

        var index = 0
        val proxy = Proxy.create<TestString> { sources[index++] }

        Assert.assertEquals("Hello, world!", proxy.value)
        Assert.assertEquals("Foo Bar", proxy.value)
    }

    @Test
    fun serializeObjectWithProxies()
    {
        val proxyObject = Proxy.create(TestString.create("Hello, world!"))
        val carrier = TestStringCarrier.create(proxyObject)

        Assert.assertEquals("{\"testString\" : {\"value\" : \"Hello, world!\"}}",
            Klaxon().toJsonString(carrier))
    }
}