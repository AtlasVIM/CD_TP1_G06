package ringmanagerclientstubs;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.68.0)",
    comments = "Source: client/ringmanagerclient.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class RingManagerClientServiceGrpc {

  private RingManagerClientServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "ringmanagerclientservice.RingManagerClientService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<ringmanagerclientstubs.VoidRequest,
      sharedstubs.PrimeServerAddress> getGetPrimeServerMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getPrimeServer",
      requestType = ringmanagerclientstubs.VoidRequest.class,
      responseType = sharedstubs.PrimeServerAddress.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ringmanagerclientstubs.VoidRequest,
      sharedstubs.PrimeServerAddress> getGetPrimeServerMethod() {
    io.grpc.MethodDescriptor<ringmanagerclientstubs.VoidRequest, sharedstubs.PrimeServerAddress> getGetPrimeServerMethod;
    if ((getGetPrimeServerMethod = RingManagerClientServiceGrpc.getGetPrimeServerMethod) == null) {
      synchronized (RingManagerClientServiceGrpc.class) {
        if ((getGetPrimeServerMethod = RingManagerClientServiceGrpc.getGetPrimeServerMethod) == null) {
          RingManagerClientServiceGrpc.getGetPrimeServerMethod = getGetPrimeServerMethod =
              io.grpc.MethodDescriptor.<ringmanagerclientstubs.VoidRequest, sharedstubs.PrimeServerAddress>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "getPrimeServer"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ringmanagerclientstubs.VoidRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  sharedstubs.PrimeServerAddress.getDefaultInstance()))
              .setSchemaDescriptor(new RingManagerClientServiceMethodDescriptorSupplier("getPrimeServer"))
              .build();
        }
      }
    }
    return getGetPrimeServerMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static RingManagerClientServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<RingManagerClientServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<RingManagerClientServiceStub>() {
        @java.lang.Override
        public RingManagerClientServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new RingManagerClientServiceStub(channel, callOptions);
        }
      };
    return RingManagerClientServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static RingManagerClientServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<RingManagerClientServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<RingManagerClientServiceBlockingStub>() {
        @java.lang.Override
        public RingManagerClientServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new RingManagerClientServiceBlockingStub(channel, callOptions);
        }
      };
    return RingManagerClientServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static RingManagerClientServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<RingManagerClientServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<RingManagerClientServiceFutureStub>() {
        @java.lang.Override
        public RingManagerClientServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new RingManagerClientServiceFutureStub(channel, callOptions);
        }
      };
    return RingManagerClientServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void getPrimeServer(ringmanagerclientstubs.VoidRequest request,
        io.grpc.stub.StreamObserver<sharedstubs.PrimeServerAddress> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetPrimeServerMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service RingManagerClientService.
   */
  public static abstract class RingManagerClientServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return RingManagerClientServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service RingManagerClientService.
   */
  public static final class RingManagerClientServiceStub
      extends io.grpc.stub.AbstractAsyncStub<RingManagerClientServiceStub> {
    private RingManagerClientServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RingManagerClientServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new RingManagerClientServiceStub(channel, callOptions);
    }

    /**
     */
    public void getPrimeServer(ringmanagerclientstubs.VoidRequest request,
        io.grpc.stub.StreamObserver<sharedstubs.PrimeServerAddress> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetPrimeServerMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service RingManagerClientService.
   */
  public static final class RingManagerClientServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<RingManagerClientServiceBlockingStub> {
    private RingManagerClientServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RingManagerClientServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new RingManagerClientServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public sharedstubs.PrimeServerAddress getPrimeServer(ringmanagerclientstubs.VoidRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetPrimeServerMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service RingManagerClientService.
   */
  public static final class RingManagerClientServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<RingManagerClientServiceFutureStub> {
    private RingManagerClientServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RingManagerClientServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new RingManagerClientServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<sharedstubs.PrimeServerAddress> getPrimeServer(
        ringmanagerclientstubs.VoidRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetPrimeServerMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_PRIME_SERVER = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_PRIME_SERVER:
          serviceImpl.getPrimeServer((ringmanagerclientstubs.VoidRequest) request,
              (io.grpc.stub.StreamObserver<sharedstubs.PrimeServerAddress>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getGetPrimeServerMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              ringmanagerclientstubs.VoidRequest,
              sharedstubs.PrimeServerAddress>(
                service, METHODID_GET_PRIME_SERVER)))
        .build();
  }

  private static abstract class RingManagerClientServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    RingManagerClientServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return ringmanagerclientstubs.Ringmanagerclient.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("RingManagerClientService");
    }
  }

  private static final class RingManagerClientServiceFileDescriptorSupplier
      extends RingManagerClientServiceBaseDescriptorSupplier {
    RingManagerClientServiceFileDescriptorSupplier() {}
  }

  private static final class RingManagerClientServiceMethodDescriptorSupplier
      extends RingManagerClientServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    RingManagerClientServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (RingManagerClientServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new RingManagerClientServiceFileDescriptorSupplier())
              .addMethod(getGetPrimeServerMethod())
              .build();
        }
      }
    }
    return result;
  }
}
