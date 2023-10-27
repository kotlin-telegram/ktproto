package ktproto.client.serialization

import kotl.core.element.TLFunction
import kotl.serialization.TL
import kotl.serialization.extensions.asTLDescriptor
import kotlinx.serialization.serializer
import ktproto.client.MTProtoClient
import kotlin.reflect.KType
import kotlin.reflect.typeOf

public suspend inline fun <reified T : MTProtoRequest<R>, reified R> MTProtoClient.execute(request: T): R {
    return execute(request, typeOf<T>(), typeOf<R>())
}

@Suppress("UNCHECKED_CAST")
public suspend fun <T, R> MTProtoClient.execute(
    request: T,
    requestType: KType,
    responseType: KType
): R {
    val function = TL.encodeToTLElement(serializer(requestType), request)
    if (function !is TLFunction) error("Can only execute TLFunction, but got $function")
    val responseSerializer = serializer(responseType)
    val responseDescriptor = responseSerializer.descriptor.asTLDescriptor()
    val expression = execute(function, responseDescriptor)
    return TL.decodeFromTLElement(responseSerializer, expression) as R
}


