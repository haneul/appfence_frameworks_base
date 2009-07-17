//#define LOG_NDEBUG 0
#define LOG_TAG "IOMX"
#include <utils/Log.h>

#include <binder/IMemory.h>
#include <binder/Parcel.h>
#include <media/IOMX.h>

namespace android {

enum {
    CONNECT = IBinder::FIRST_CALL_TRANSACTION,
    LIST_NODES,
    ALLOCATE_NODE,
    FREE_NODE,
    SEND_COMMAND,
    GET_PARAMETER,
    SET_PARAMETER,
    USE_BUFFER,
    ALLOC_BUFFER,
    ALLOC_BUFFER_WITH_BACKUP,
    FREE_BUFFER,
    OBSERVE_NODE,
    FILL_BUFFER,
    EMPTY_BUFFER,
    OBSERVER_ON_MSG,
};

static void *readVoidStar(const Parcel *parcel) {
    // FIX if sizeof(void *) != sizeof(int32)
    return (void *)parcel->readInt32();
}

static void writeVoidStar(void *x, Parcel *parcel) {
    // FIX if sizeof(void *) != sizeof(int32)
    parcel->writeInt32((int32_t)x);
}

class BpOMX : public BpInterface<IOMX> {
public:
    BpOMX(const sp<IBinder> &impl)
        : BpInterface<IOMX>(impl) {
    }

#if IOMX_USES_SOCKETS
    virtual status_t connect(int *sd) {
        Parcel data, reply;
        data.writeInterfaceToken(IOMX::getInterfaceDescriptor());
        remote()->transact(CONNECT, data, &reply);

        status_t err = reply.readInt32();
        if (err == OK) {
            *sd = dup(reply.readFileDescriptor());
        } else {
            *sd = -1;
        }

        return reply.readInt32();
    }
#endif

    virtual status_t list_nodes(List<String8> *list) {
        list->clear();

        Parcel data, reply;
        data.writeInterfaceToken(IOMX::getInterfaceDescriptor());
        remote()->transact(LIST_NODES, data, &reply);

        int32_t n = reply.readInt32();
        for (int32_t i = 0; i < n; ++i) {
            String8 s = reply.readString8();

            list->push_back(s);
        }

        return OK;
    }

    virtual status_t allocate_node(const char *name, node_id *node) {
        Parcel data, reply;
        data.writeInterfaceToken(IOMX::getInterfaceDescriptor());
        data.writeCString(name);
        remote()->transact(ALLOCATE_NODE, data, &reply);

        status_t err = reply.readInt32();
        if (err == OK) {
            *node = readVoidStar(&reply);
        } else {
            *node = 0;
        }

        return err;
    }

    virtual status_t free_node(node_id node) {
        Parcel data, reply;
        data.writeInterfaceToken(IOMX::getInterfaceDescriptor());
        writeVoidStar(node, &data);
        remote()->transact(FREE_NODE, data, &reply);

        return reply.readInt32();
    }

    virtual status_t send_command(
            node_id node, OMX_COMMANDTYPE cmd, OMX_S32 param) {
        Parcel data, reply;
        data.writeInterfaceToken(IOMX::getInterfaceDescriptor());
        writeVoidStar(node, &data);
        data.writeInt32(cmd);
        data.writeInt32(param);
        remote()->transact(SEND_COMMAND, data, &reply);

        return reply.readInt32();
    }

    virtual status_t get_parameter(
            node_id node, OMX_INDEXTYPE index,
            void *params, size_t size) {
        Parcel data, reply;
        data.writeInterfaceToken(IOMX::getInterfaceDescriptor());
        writeVoidStar(node, &data);
        data.writeInt32(index);
        data.writeInt32(size);
        data.write(params, size);
        remote()->transact(GET_PARAMETER, data, &reply);

        status_t err = reply.readInt32();
        if (err != OK) {
            return err;
        }

        reply.read(params, size);

        return OK;
    }

    virtual status_t set_parameter(
            node_id node, OMX_INDEXTYPE index,
            const void *params, size_t size) {
        Parcel data, reply;
        data.writeInterfaceToken(IOMX::getInterfaceDescriptor());
        writeVoidStar(node, &data);
        data.writeInt32(index);
        data.writeInt32(size);
        data.write(params, size);
        remote()->transact(SET_PARAMETER, data, &reply);

        return reply.readInt32();
    }

    virtual status_t use_buffer(
            node_id node, OMX_U32 port_index, const sp<IMemory> &params,
            buffer_id *buffer) {
        Parcel data, reply;
        data.writeInterfaceToken(IOMX::getInterfaceDescriptor());
        writeVoidStar(node, &data);
        data.writeInt32(port_index);
        data.writeStrongBinder(params->asBinder());
        remote()->transact(USE_BUFFER, data, &reply);

        status_t err = reply.readInt32();
        if (err != OK) {
            *buffer = 0;

            return err;
        }

        *buffer = readVoidStar(&reply);

        return err;
    }

    virtual status_t allocate_buffer(
            node_id node, OMX_U32 port_index, size_t size,
            buffer_id *buffer) {
        Parcel data, reply;
        data.writeInterfaceToken(IOMX::getInterfaceDescriptor());
        writeVoidStar(node, &data);
        data.writeInt32(port_index);
        data.writeInt32(size);
        remote()->transact(ALLOC_BUFFER, data, &reply);

        status_t err = reply.readInt32();
        if (err != OK) {
            *buffer = 0;

            return err;
        }

        *buffer = readVoidStar(&reply);

        return err;
    }

    virtual status_t allocate_buffer_with_backup(
            node_id node, OMX_U32 port_index, const sp<IMemory> &params,
            buffer_id *buffer) {
        Parcel data, reply;
        data.writeInterfaceToken(IOMX::getInterfaceDescriptor());
        writeVoidStar(node, &data);
        data.writeInt32(port_index);
        data.writeStrongBinder(params->asBinder());
        remote()->transact(ALLOC_BUFFER_WITH_BACKUP, data, &reply);

        status_t err = reply.readInt32();
        if (err != OK) {
            *buffer = 0;

            return err;
        }

        *buffer = readVoidStar(&reply);

        return err;
    }

    virtual status_t free_buffer(
            node_id node, OMX_U32 port_index, buffer_id buffer) {
        Parcel data, reply;
        data.writeInterfaceToken(IOMX::getInterfaceDescriptor());
        writeVoidStar(node, &data);
        data.writeInt32(port_index);
        writeVoidStar(buffer, &data);
        remote()->transact(FREE_BUFFER, data, &reply);

        return reply.readInt32();
    }

#if !IOMX_USES_SOCKETS
    virtual status_t observe_node(
            node_id node, const sp<IOMXObserver> &observer) {
        Parcel data, reply;
        data.writeInterfaceToken(IOMX::getInterfaceDescriptor());
        writeVoidStar(node, &data);
        data.writeStrongBinder(observer->asBinder());
        remote()->transact(OBSERVE_NODE, data, &reply);

        return reply.readInt32();
    }

    virtual void fill_buffer(node_id node, buffer_id buffer) {
        Parcel data, reply;
        data.writeInterfaceToken(IOMX::getInterfaceDescriptor());
        writeVoidStar(node, &data);
        writeVoidStar(buffer, &data);
        remote()->transact(FILL_BUFFER, data, &reply, IBinder::FLAG_ONEWAY);
    }

    virtual void empty_buffer(
            node_id node,
            buffer_id buffer,
            OMX_U32 range_offset, OMX_U32 range_length,
            OMX_U32 flags, OMX_TICKS timestamp) {
        Parcel data, reply;
        data.writeInterfaceToken(IOMX::getInterfaceDescriptor());
        writeVoidStar(node, &data);
        writeVoidStar(buffer, &data);
        data.writeInt32(range_offset);
        data.writeInt32(range_length);
        data.writeInt32(flags);
        data.writeInt64(timestamp);
        remote()->transact(EMPTY_BUFFER, data, &reply, IBinder::FLAG_ONEWAY);
    }
#endif
};

IMPLEMENT_META_INTERFACE(OMX, "android.hardware.IOMX");

////////////////////////////////////////////////////////////////////////////////

#define CHECK_INTERFACE(interface, data, reply) \
        do { if (!data.enforceInterface(interface::getInterfaceDescriptor())) { \
            LOGW("Call incorrectly routed to " #interface); \
            return PERMISSION_DENIED; \
        } } while (0)

status_t BnOMX::onTransact(
    uint32_t code, const Parcel &data, Parcel *reply, uint32_t flags) {
    switch (code) {
#if IOMX_USES_SOCKETS
        case CONNECT:
        {
            CHECK_INTERFACE(IOMX, data, reply);

            int s;
            status_t err = connect(&s);
            
            reply->writeInt32(err);
            if (err == OK) {
                assert(s >= 0);
                reply->writeDupFileDescriptor(s);
                close(s);
                s = -1;
            } else {
                assert(s == -1);
            }

            return NO_ERROR;
        }
#endif

        case LIST_NODES:
        {
            CHECK_INTERFACE(IOMX, data, reply);

            List<String8> list;
            list_nodes(&list);

            reply->writeInt32(list.size());
            for (List<String8>::iterator it = list.begin();
                 it != list.end(); ++it) {
                reply->writeString8(*it);
            }

            return NO_ERROR;
        }

        case ALLOCATE_NODE:
        {
            CHECK_INTERFACE(IOMX, data, reply);

            node_id node;
            status_t err = allocate_node(data.readCString(), &node);
            reply->writeInt32(err);
            if (err == OK) {
                writeVoidStar(node, reply);
            }
                
            return NO_ERROR;
        }

        case FREE_NODE:
        {
            CHECK_INTERFACE(IOMX, data, reply);

            node_id node = readVoidStar(&data);

            reply->writeInt32(free_node(node));
                
            return NO_ERROR;
        }

        case SEND_COMMAND:
        {
            CHECK_INTERFACE(IOMX, data, reply);

            node_id node = readVoidStar(&data);

            OMX_COMMANDTYPE cmd =
                static_cast<OMX_COMMANDTYPE>(data.readInt32());

            OMX_S32 param = data.readInt32();
            reply->writeInt32(send_command(node, cmd, param));

            return NO_ERROR;
        }

        case GET_PARAMETER:
        {
            CHECK_INTERFACE(IOMX, data, reply);

            node_id node = readVoidStar(&data);
            OMX_INDEXTYPE index = static_cast<OMX_INDEXTYPE>(data.readInt32());

            size_t size = data.readInt32();

            // XXX I am not happy with this but Parcel::readInplace didn't work.
            void *params = malloc(size);
            data.read(params, size);

            status_t err = get_parameter(node, index, params, size);

            reply->writeInt32(err);

            if (err == OK) {
                reply->write(params, size);
            }

            free(params);
            params = NULL;

            return NO_ERROR;
        }

        case SET_PARAMETER:
        {
            CHECK_INTERFACE(IOMX, data, reply);

            node_id node = readVoidStar(&data);
            OMX_INDEXTYPE index = static_cast<OMX_INDEXTYPE>(data.readInt32());

            size_t size = data.readInt32();
            void *params = const_cast<void *>(data.readInplace(size));

            reply->writeInt32(set_parameter(node, index, params, size));

            return NO_ERROR;
        }

        case USE_BUFFER:
        {
            CHECK_INTERFACE(IOMX, data, reply);

            node_id node = readVoidStar(&data);
            OMX_U32 port_index = data.readInt32();
            sp<IMemory> params =
                interface_cast<IMemory>(data.readStrongBinder());

            buffer_id buffer;
            status_t err = use_buffer(node, port_index, params, &buffer);
            reply->writeInt32(err);

            if (err == OK) {
                writeVoidStar(buffer, reply);
            }

            return NO_ERROR;
        }

        case ALLOC_BUFFER:
        {
            CHECK_INTERFACE(IOMX, data, reply);

            node_id node = readVoidStar(&data);
            OMX_U32 port_index = data.readInt32();
            size_t size = data.readInt32();

            buffer_id buffer;
            status_t err = allocate_buffer(node, port_index, size, &buffer);
            reply->writeInt32(err);

            if (err == OK) {
                writeVoidStar(buffer, reply);
            }

            return NO_ERROR;
        }

        case ALLOC_BUFFER_WITH_BACKUP:
        {
            CHECK_INTERFACE(IOMX, data, reply);

            node_id node = readVoidStar(&data);
            OMX_U32 port_index = data.readInt32();
            sp<IMemory> params =
                interface_cast<IMemory>(data.readStrongBinder());

            buffer_id buffer;
            status_t err = allocate_buffer_with_backup(
                    node, port_index, params, &buffer);

            reply->writeInt32(err);

            if (err == OK) {
                writeVoidStar(buffer, reply);
            }

            return NO_ERROR;
        }

        case FREE_BUFFER:
        {
            CHECK_INTERFACE(IOMX, data, reply);

            node_id node = readVoidStar(&data);
            OMX_U32 port_index = data.readInt32();
            buffer_id buffer = readVoidStar(&data);
            reply->writeInt32(free_buffer(node, port_index, buffer));

            return NO_ERROR;
        }

#if !IOMX_USES_SOCKETS
        case OBSERVE_NODE:
        {
            CHECK_INTERFACE(IOMX, data, reply);

            node_id node = readVoidStar(&data);
            sp<IOMXObserver> observer =
                interface_cast<IOMXObserver>(data.readStrongBinder());
            reply->writeInt32(observe_node(node, observer));

            return NO_ERROR;
        }

        case FILL_BUFFER:
        {
            CHECK_INTERFACE(IOMX, data, reply);

            node_id node = readVoidStar(&data);
            buffer_id buffer = readVoidStar(&data);
            fill_buffer(node, buffer);

            return NO_ERROR;
        }

        case EMPTY_BUFFER:
        {
            CHECK_INTERFACE(IOMX, data, reply);

            node_id node = readVoidStar(&data);
            buffer_id buffer = readVoidStar(&data);
            OMX_U32 range_offset = data.readInt32();
            OMX_U32 range_length = data.readInt32();
            OMX_U32 flags = data.readInt32();
            OMX_TICKS timestamp = data.readInt64();

            empty_buffer(
                    node, buffer, range_offset, range_length,
                    flags, timestamp);

            return NO_ERROR;
        }
#endif

        default:
            return BBinder::onTransact(code, data, reply, flags);
    }
}

////////////////////////////////////////////////////////////////////////////////

class BpOMXObserver : public BpInterface<IOMXObserver> {
public:
    BpOMXObserver(const sp<IBinder> &impl)
        : BpInterface<IOMXObserver>(impl) {
    }

    virtual void on_message(const omx_message &msg) {
        Parcel data, reply;
        data.writeInterfaceToken(IOMXObserver::getInterfaceDescriptor());
        data.write(&msg, sizeof(msg));

        remote()->transact(OBSERVER_ON_MSG, data, &reply, IBinder::FLAG_ONEWAY);
    }
};

IMPLEMENT_META_INTERFACE(OMXObserver, "android.hardware.IOMXObserver");

status_t BnOMXObserver::onTransact(
    uint32_t code, const Parcel &data, Parcel *reply, uint32_t flags) {
    switch (code) {
        case OBSERVER_ON_MSG:
        {
            CHECK_INTERFACE(IOMXObserver, data, reply);

            omx_message msg;
            data.read(&msg, sizeof(msg));

            // XXX Could use readInplace maybe?
            on_message(msg);

            return NO_ERROR;
        }

        default:
            return BBinder::onTransact(code, data, reply, flags);
    }
}

}  // namespace android