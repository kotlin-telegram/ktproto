package ktproto.client.serialization

import kotl.core.element.TLFunction
import kotl.serialization.TL
import kotl.serialization.decodeFromTLElement
import kotl.serialization.extensions.asTLDescriptor
import kotlinx.serialization.serializer
import ktproto.client.MTProtoClient
import ktproto.client.MTProtoRequest
import kotlin.reflect.typeOf

public suspend inline fun <reified T : MTProtoRequestContainer<R>, reified R> MTProtoClient.execute(request: T): R {
    val descriptor = MTProtoRequestDescriptor<T, R>(typeOf<T>(), typeOf<R>())
    return execute(request, descriptor)
}

@Suppress("UNCHECKED_CAST")
public suspend fun <T, R> MTProtoClient.execute(
    request: T,
    descriptor: MTProtoRequestDescriptor<T, R>
): R {
    val function = TL.encodeToTLElement(serializer(descriptor.functionType), request)
    if (function !is TLFunction) error("Can only execute TLFunction, but got $function")
    val responseSerializer = serializer(descriptor.returnType)
    val responseDescriptor = responseSerializer.descriptor.asTLDescriptor()
    val mtProtoRequest = MTProtoRequest(function, responseDescriptor)
    val expression =  execute(mtProtoRequest)
    return TL.decodeFromTLElement(responseSerializer, expression) as R
}
