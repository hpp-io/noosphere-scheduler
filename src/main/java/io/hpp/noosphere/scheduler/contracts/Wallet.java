package io.hpp.noosphere.scheduler.contracts;

import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.StaticStruct;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Bytes4;
import org.web3j.abi.datatypes.generated.Uint16;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.10.3.
 */
@SuppressWarnings("rawtypes")
public class Wallet extends Contract {
    public static final String BINARY = "60a0346200012957601f620022c338819003918201601f19168301916001600160401b038311848410176200012d578084926040948552833981010312620001295760206200004e8262000141565b916001600160a01b0391829162000066910162000141565b1691821562000111575f80546001600160a01b03198116851782556040519491908416907f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e09080a3169081156200010257506080526001805560405161216c908162000157823960805181818161033a0152818161066e01528181610a4e01528181610bea01528181611011015281816113cf015261170a0152f35b632530e88560e11b8152600490fd5b604051631e4fbdf760e01b81525f6004820152602490fd5b5f80fd5b634e487b7160e01b5f52604160045260245ffd5b51906001600160a01b0382168203620001295756fe60806040908082526004908136101561004b575b5050361561001f575f80fd5b7fe1fffcc4923d04b559f4d29a8bfc6cda04eb5b0d3c460751c2402c5c5cc9109c60205f9251348152a2005b5f3560e01c90816311d8668c14611694575080631626ba7e146115d9578063181f5a77146114cd578063303d0c6b146113a3578063711dc79214610fa3578063715018a614610f095780637c72ccf614610ea75780638612d04914610e305780638da5cb5b14610ddf57806394862f9c14610d75578063a04889e914610b77578063b676899814610a22578063b9b3e06a146109ad578063c3909fa11461094b578063cb7aa2fa14610616578063dd62ed3e146105a2578063e1f21c671461052f578063ec8669bd146102ed578063f2fde38b1461020c5763f3fef3a3146101335780610013565b903461020857807ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffc3601126102085761016a6119de565b9060243592610177611f38565b61017f611c9c565b61018883611cd7565b84116101e1575073ffffffffffffffffffffffffffffffffffffffff7f884edad9ce6fa2440d8a54cc123490eb96d2768479d49ff9c7366125a9424364926020926101d4863384611d9d565b519485521692a260018055005b90517f356680b7000000000000000000000000000000000000000000000000000000008152fd5b5f80fd5b50346102085760207ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffc360112610208576102446119de565b9061024d611f38565b73ffffffffffffffffffffffffffffffffffffffff8092169283156102be5750505f54827fffffffffffffffffffffffff00000000000000000000000000000000000000008216175f55167f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e05f80a3005b905f60249251917f1e4fbdf7000000000000000000000000000000000000000000000000000000008352820152fd5b509034610208576020807ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffc36011261020857823573ffffffffffffffffffffffffffffffffffffffff91827f000000000000000000000000000000000000000000000000000000000000000016330361050657610368611c9c565b815f5260058152835f209284519061037f82611a24565b808554168252806001860154169483830195865260028101548784015260ff8860038301549260608601938452015461ffff80821660808701528160101c1660a0860152851c1615801560c08501526104de575191818151165f5260038452865f20828751165f528452865f20548084116104b657907f7d370a94e557d98fc60928112c72d25d1a18d792043237cc9e3715666c9e61e195969798610425858594611b79565b838351165f52600387528a5f20848b51165f5287528a5f2055828951165f5260028652895f20610456868254611b79565b9055828251165f528552885f20828951165f528552885f20610479858254611bb3565b9055865f52600585526104a5895f2060045f918281558260018201558260028201558260038201550155565b51169551169551908152a460018055005b8888517f39c8bcf9000000000000000000000000000000000000000000000000000000008152fd5b8787517ff0da79df000000000000000000000000000000000000000000000000000000008152fd5b505050517fc41a5b09000000000000000000000000000000000000000000000000000000008152fd5b5034610208577f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925602061056136611ae8565b9161056d969196611f38565b73ffffffffffffffffffffffffffffffffffffffff80911695865f528452815f20961695865f52835281815f205551908152a3005b50903461020857807ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffc360112610208576020916105dd6119de565b6105e5611a01565b9173ffffffffffffffffffffffffffffffffffffffff8092165f528452825f2091165f528252805f20549051908152f35b5090346102085760607ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffc360112610208578135610651611a01565b6044359273ffffffffffffffffffffffffffffffffffffffff94857f00000000000000000000000000000000000000000000000000000000000000001633036109245761069c611c9c565b835f526005602052815f20928184019586549160ff8360201c16156108fc5781156108d4576003860191825481116108ac5761ffff93848082169160101c1610156108845791869593918995938b809954165f526003602052897ffab66afb795acabdb9d25b8483330bd32d4ac9b22e83e44ae20cffc94101a33e8a86895f209a60018d019b838d54165f526020528a5f20610739878254611b79565b9055828c54165f5260026020528a5f20610754878254611b79565b9055610761868954611b79565b885580547fffffffffffffffffffffffffffffffffffffffffffffffffffffffff0000ffff8116601091821c8416600101841690911b63ffff0000161781555460101c16996107b4858784845416611d9d565b54895194855261ffff8b166020860152169d8e941692604090a454938415918215998a610877575b5050506107ea575b60018055005b7f7d370a94e557d98fc60928112c72d25d1a18d792043237cc9e3715666c9e61e194602094541696610854575b50845f5260058352610842815f2060045f918281558260018201558260028201558260038201550155565b51908152a45f808080808080806107e4565b865f528352805f20875f528352805f2061086f838254611bb3565b90555f610817565b54161490505f80806107dc565b8486517f3a082e21000000000000000000000000000000000000000000000000000000008152fd5b8486517f5d4b1077000000000000000000000000000000000000000000000000000000008152fd5b8385517f1f2a2005000000000000000000000000000000000000000000000000000000008152fd5b8385517ff0da79df000000000000000000000000000000000000000000000000000000008152fd5b90517fc41a5b09000000000000000000000000000000000000000000000000000000008152fd5b82346102085760207ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffc3601126102085760209073ffffffffffffffffffffffffffffffffffffffff61099b6119de565b165f5260028252805f20549051908152f35b823461020857807ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffc360112610208576020906109e76119de565b6109ef611a01565b9073ffffffffffffffffffffffffffffffffffffffff8091165f5260038452825f2091165f528252805f20549051908152f35b50903461020857610a3236611ae8565b73ffffffffffffffffffffffffffffffffffffffff93919392837f0000000000000000000000000000000000000000000000000000000000000000163303610b4f578390610a7e611c9c565b1692835f5260209060038252835f20951694855f528152825f20548211610b27575f908495967f813582499997f00ba0142c7813740a6e381df71a63d11d8c8f208f66b7795d26958352600382528483208884528252848320610ae2858254611b79565b905587835260028252848320610af9858254611b79565b905586835281528382208783528152838220610b16848254611bb3565b90558351928352820152a360018055005b8583517f356680b7000000000000000000000000000000000000000000000000000000008152fd5b8583517fc41a5b09000000000000000000000000000000000000000000000000000000008152fd5b50903461020857807ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffc36011261020857610baf6119de565b9060243567ffffffffffffffff811161020857610bcf9036908501611b48565b909273ffffffffffffffffffffffffffffffffffffffff91827f0000000000000000000000000000000000000000000000000000000000000000163303610d4d57610c18611c9c565b5f5b818110610c275760018055005b610c32818388611c3e565b8581013580610c46575b5050600101610c1a565b85851690815f5260208a81528a895f2091808601928a610c6585611c7b565b165f5281528a5f2054848110610d2557610ced7fd1398bee19313d6bf672ccb116e51f4a1a947e91c757907f51fbb5b5e56c698f9486948e8e95610cb160019d9c9b9a610cf397611b79565b928a5f528152815f209087610cc586611c7b565b165f52525f2055610ce885610cd983611c7b565b610ce28b611c7b565b90611d9d565b611c7b565b95611c7b565b8b5173ffffffffffffffffffffffffffffffffffffffff96909616865260208601929092521692604090a3905f610c3c565b828c517f13be252b000000000000000000000000000000000000000000000000000000008152fd5b8584517fc41a5b09000000000000000000000000000000000000000000000000000000008152fd5b5090346102085760207ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffc3601126102085781602092355f5260058352815f20015460ff81841c165f14610dd45761ffff809160101c16915b5191168152f35b5061ffff5f91610dcd565b8234610208575f7ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffc3601126102085760209073ffffffffffffffffffffffffffffffffffffffff5f54169051908152f35b823461020857807ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffc36011261020857602090610e6a6119de565b610e72611a01565b9073ffffffffffffffffffffffffffffffffffffffff8091165f5260038452825f2091165f528252805f205415159051908152f35b5090346102085760207ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffc3601126102085781602092355f526005835260ff825f2091820154841c165f14610f015760030154905b51908152f35b505f90610efb565b34610208575f7ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffc36011261020857610f3f611f38565b5f73ffffffffffffffffffffffffffffffffffffffff81547fffffffffffffffffffffffff000000000000000000000000000000000000000081168355167f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e08280a3005b50903461020857807ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffc3601126102085781356024803567ffffffffffffffff811161020857610ff59036908601611b48565b73ffffffffffffffffffffffffffffffffffffffff94919491827f000000000000000000000000000000000000000000000000000000000000000016330361137b5761103f611c9c565b845f5260209260058452815f209288840180549060ff82881c16156113535761ffff91828082169160101c16101561132b575f995f9a600188019b868d54169a5b86821061128f57505060038801998a548211611267578689541690815f5260038b52885f20815f528b52885f205480841161123f57906110c5846110f9959493611b79565b915f5260038c52895f20905f528b52885f2055868d54165f5260028a52875f206110f0828254611b79565b90558a54611b79565b895581547fffffffffffffffffffffffffffffffffffffffffffffffffffffffff0000ffff8116601091821c8516600101851690911b63ffff000016178255815460101c8316938a5f888e5b8483106111ba5750505050505054161461115f5760018055005b807f7d370a94e557d98fc60928112c72d25d1a18d792043237cc9e3715666c9e61e1955493541696541696826108545750845f5260058352610842815f2060045f918281558260018201558260028201558260038201550155565b6001937ffab66afb795acabdb9d25b8483330bd32d4ac9b22e83e44ae20cffc94101a33e8b6111ea86898b611c3e565b936112328d836112118185541698610ce861120482611c7b565b9a8c8301359b8c91611d9d565b935416975193849316968390929161ffff6020916040840195845216910152565b0390a4018b90888e611145565b8f8a517f39c8bcf9000000000000000000000000000000000000000000000000000000008152fd5b8d88517f5d4b1077000000000000000000000000000000000000000000000000000000008152fd5b90918b886112a88d6112a2878c8a611c3e565b01611c7b565b16036112d0576112c86001918a6112c0868b89611c3e565b013590611bb3565b920190611080565b60648f6018848e8d51937f08c379a00000000000000000000000000000000000000000000000000000000085528401528201527f4d69736d617463686564207061796d656e7420746f6b656e00000000000000006044820152fd5b8a85517f3a082e21000000000000000000000000000000000000000000000000000000008152fd5b8a85517ff0da79df000000000000000000000000000000000000000000000000000000008152fd5b8690517fc41a5b09000000000000000000000000000000000000000000000000000000008152fd5b509034610208576113b336611ae8565b73ffffffffffffffffffffffffffffffffffffffff93919392837f0000000000000000000000000000000000000000000000000000000000000000163303610b4f576113fd611c9c565b61140685611cd7565b8211610b2757831692835f52602090868252835f20951694855f52815281835f2054106114a5576001908495967f813582499997f00ba0142c7813740a6e381df71a63d11d8c8f208f66b7795d26955f528152835f20875f528152835f2061146f848254611b79565b9055855f5260038152835f20875f528152835f2061148e848254611bb3565b9055865f5260028152835f20610b16848254611bb3565b8583517f13be252b000000000000000000000000000000000000000000000000000000008152fd5b509034610208575f7ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffc360112610208578051918183019083821067ffffffffffffffff8311176115ad57508152600c825260207f57616c6c657420312e302e30000000000000000000000000000000000000000060208401528151928391602083528151918260208501525f5b8381106115975750505f83830185015250601f017fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffe0168101030190f35b818101830151878201870152869450820161155a565b6041907f4e487b71000000000000000000000000000000000000000000000000000000005f525260245ffd5b50903461020857807ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffc3601126102085760243567ffffffffffffffff8111610208573660238201121561020857808301359061163482611aae565b61164084519182611a6d565b8281523660248484010111610208576020945f86857fffffffff0000000000000000000000000000000000000000000000000000000096602461168c9701838701378401015235611bc0565b915191168152f35b9050346102085760a07ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffc360112610208576116cd6119de565b926116d6611a01565b9360443591606435936084359261ffff808516928386036102085773ffffffffffffffffffffffffffffffffffffffff90817f00000000000000000000000000000000000000000000000000000000000000001633036119b75750611739611c9c565b875f526020926005845260ff8a875f200154851c1661198f5761175b8b611cd7565b881161196757811698895f5280845281865f209b169a8b5f52845287865f2054106119405790828993928b5f52808652875f208d5f528652875f208a8154906117a391611b79565b90558b5f5260038652875f208d5f528652875f208a8154906117c491611bb3565b90558c5f5260028652875f208a8154906117dd91611bb3565b905587516117ea81611a24565b8c81528d8782019081528982018c815260608301918d8352608084019a8b5260a08401965f885260c085019960018b525f5260058b52808d5f20955116907fffffffffffffffffffffffff0000000000000000000000000000000000000000918287541617865560018601925116908254161790555160028301555160038201550195511685547fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff00001617855551166118d29084907fffffffffffffffffffffffffffffffffffffffffffffffffffffffff0000ffff63ffff000083549260101b169116179055565b5182547fffffffffffffffffffffffffffffffffffffffffffffffffffffff00ffffffff1664ff0000000091151590921b161790555191825261ffff1660208201527f21a0cbcf4ecd8cd3ff869836abc07ebc6ecf06048838bc149520ad654264f03390604090a460018055005b85517f13be252b000000000000000000000000000000000000000000000000000000008152fd5b8986517f356680b7000000000000000000000000000000000000000000000000000000008152fd5b8986517f85112af6000000000000000000000000000000000000000000000000000000008152fd5b807fc41a5b09000000000000000000000000000000000000000000000000000000008b9252fd5b6004359073ffffffffffffffffffffffffffffffffffffffff8216820361020857565b6024359073ffffffffffffffffffffffffffffffffffffffff8216820361020857565b60e0810190811067ffffffffffffffff821117611a4057604052565b7f4e487b71000000000000000000000000000000000000000000000000000000005f52604160045260245ffd5b90601f7fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffe0910116810190811067ffffffffffffffff821117611a4057604052565b67ffffffffffffffff8111611a4057601f017fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffe01660200190565b7ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffc60609101126102085773ffffffffffffffffffffffffffffffffffffffff90600435828116810361020857916024359081168103610208579060443590565b9181601f840112156102085782359167ffffffffffffffff8311610208576020808501946060850201011161020857565b91908203918211611b8657565b7f4e487b71000000000000000000000000000000000000000000000000000000005f52601160045260245ffd5b91908201809211611b8657565b611bd691611bcd91611f88565b90929192611fc2565b73ffffffffffffffffffffffffffffffffffffffff805f5416911614611c1a577fffffffff0000000000000000000000000000000000000000000000000000000090565b7f1626ba7e0000000000000000000000000000000000000000000000000000000090565b9190811015611c4e576060020190565b7f4e487b71000000000000000000000000000000000000000000000000000000005f52603260045260245ffd5b3573ffffffffffffffffffffffffffffffffffffffff811681036102085790565b600260015414611cad576002600155565b60046040517f3ee5aeb5000000000000000000000000000000000000000000000000000000008152fd5b73ffffffffffffffffffffffffffffffffffffffff16805f52600260205260405f205481155f14611d1057611d0d915047611b79565b90565b6020602492604051938480927f70a082310000000000000000000000000000000000000000000000000000000082523060048301525afa8015611d92575f90611d5e575b611d0d9250611b79565b506020823d602011611d8a575b81611d7860209383611a6d565b8101031261020857611d0d9151611d54565b3d9150611d6b565b6040513d5f823e3d90fd5b73ffffffffffffffffffffffffffffffffffffffff9190821680611e775750824710611e3f575f809381938293165af13d15611e37573d90611dde82611aae565b91611dec6040519384611a6d565b82523d5f602084013e5b15611dfe5750565b805115611e0d57602081519101fd5b60046040517fd6bda275000000000000000000000000000000000000000000000000000000008152fd5b606090611df6565b6044834790604051917fcf47918100000000000000000000000000000000000000000000000000000000835260048301526024820152fd5b6040517fa9059cbb00000000000000000000000000000000000000000000000000000000602080830191825273ffffffffffffffffffffffffffffffffffffffff94909416602483015260448083019690965294815290939192505f9190611ee0606482611a6d565b519082855af115611d92575f513d611f2f5750803b155b611efe5750565b602490604051907f5274afe70000000000000000000000000000000000000000000000000000000082526004820152fd5b60011415611ef7565b73ffffffffffffffffffffffffffffffffffffffff5f54163303611f5857565b60246040517f118cdaa7000000000000000000000000000000000000000000000000000000008152336004820152fd5b8151919060418303611fb857611fb19250602082015190606060408401519301515f1a906120a7565b9192909190565b50505f9160029190565b600481101561207a5780611fd4575050565b600181036120065760046040517ff645eedf000000000000000000000000000000000000000000000000000000008152fd5b6002810361203f57602482604051907ffce698f70000000000000000000000000000000000000000000000000000000082526004820152fd5b6003146120495750565b602490604051907fd78bce0c0000000000000000000000000000000000000000000000000000000082526004820152fd5b7f4e487b71000000000000000000000000000000000000000000000000000000005f52602160045260245ffd5b91907f7fffffffffffffffffffffffffffffff5d576e7357a4501ddfe92f46681b20a0841161212b579160209360809260ff5f9560405194855216868401526040830152606082015282805260015afa15611d92575f5173ffffffffffffffffffffffffffffffffffffffff81161561212157905f905f90565b505f906001905f90565b5050505f916003919056fea26469706673582212208cbab4910b87f64a9250d688c614d72aee1100d306c728683ad92e7ee41d91c364736f6c63430008170033";

    public static final String FUNC_ALLOWANCE = "allowance";

    public static final String FUNC_APPROVE = "approve";

    public static final String FUNC_DISBURSEFORFULFILLMENT = "disburseForFulfillment";

    public static final String FUNC_DISBURSEFORREQUEST = "disburseForRequest";

    public static final String FUNC_ISLOCKED = "isLocked";

    public static final String FUNC_ISVALIDSIGNATURE = "isValidSignature";

    public static final String FUNC_LOCKESCROW = "lockEscrow";

    public static final String FUNC_LOCKFORREQUEST = "lockForRequest";

    public static final String FUNC_LOCKEDOF = "lockedOf";

    public static final String FUNC_LOCKEDOFREQUEST = "lockedOfRequest";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_PAIDCOUNTOFREQUEST = "paidCountOfRequest";

    public static final String FUNC_RELEASEESCROW = "releaseEscrow";

    public static final String FUNC_RELEASEFORREQUEST = "releaseForRequest";

    public static final String FUNC_RENOUNCEOWNERSHIP = "renounceOwnership";

    public static final String FUNC_TOTALLOCKEDFOR = "totalLockedFor";

    public static final String FUNC_TRANSFERBYROUTER = "transferByRouter";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final String FUNC_TYPEANDVERSION = "typeAndVersion";

    public static final String FUNC_WITHDRAW = "withdraw";

    public static final Event APPROVAL_EVENT = new Event("Approval", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event DEPOSIT_EVENT = new Event("Deposit", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event ESCROW_EVENT = new Event("Escrow", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}, new TypeReference<Bool>() {}));
    ;

    public static final Event OWNERSHIPTRANSFERRED_EVENT = new Event("OwnershipTransferred", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    public static final Event REQUESTDISBURSED_EVENT = new Event("RequestDisbursed", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}, new TypeReference<Uint16>() {}));
    ;

    public static final Event REQUESTLOCKED_EVENT = new Event("RequestLocked", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}, new TypeReference<Uint16>() {}));
    ;

    public static final Event REQUESTRELEASED_EVENT = new Event("RequestReleased", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event TRANSFER_EVENT = new Event("Transfer", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>() {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event WITHDRAW_EVENT = new Event("Withdraw", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    @Deprecated
    protected Wallet(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Wallet(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Wallet(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Wallet(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<BigInteger> allowance(String param0, String param1) {
        final Function function = new Function(FUNC_ALLOWANCE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, param0), 
                new org.web3j.abi.datatypes.Address(160, param1)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> approve(String spender, String token, BigInteger amount) {
        final Function function = new Function(
                FUNC_APPROVE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, spender), 
                new org.web3j.abi.datatypes.Address(160, token), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> disburseForFulfillment(byte[] requestId, List<Payment> payments) {
        final Function function = new Function(
                FUNC_DISBURSEFORFULFILLMENT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(requestId), 
                new org.web3j.abi.datatypes.DynamicArray<Payment>(Payment.class, payments)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> disburseForRequest(byte[] requestId, String to, BigInteger amount) {
        final Function function = new Function(
                FUNC_DISBURSEFORREQUEST, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(requestId), 
                new org.web3j.abi.datatypes.Address(160, to), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Boolean> isLocked(String spender, String token) {
        final Function function = new Function(FUNC_ISLOCKED, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, spender), 
                new org.web3j.abi.datatypes.Address(160, token)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<byte[]> isValidSignature(byte[] hash_, byte[] signature_) {
        final Function function = new Function(FUNC_ISVALIDSIGNATURE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(hash_), 
                new org.web3j.abi.datatypes.DynamicBytes(signature_)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes4>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<TransactionReceipt> lockEscrow(String spender, String token, BigInteger amount) {
        final Function function = new Function(
                FUNC_LOCKESCROW, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, spender), 
                new org.web3j.abi.datatypes.Address(160, token), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> lockForRequest(String spender, String token, BigInteger totalAmount, byte[] requestId, BigInteger redundancy) {
        final Function function = new Function(
                FUNC_LOCKFORREQUEST, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, spender), 
                new org.web3j.abi.datatypes.Address(160, token), 
                new org.web3j.abi.datatypes.generated.Uint256(totalAmount), 
                new org.web3j.abi.datatypes.generated.Bytes32(requestId), 
                new org.web3j.abi.datatypes.generated.Uint16(redundancy)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> lockedOf(String spender, String token) {
        final Function function = new Function(FUNC_LOCKEDOF, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, spender), 
                new org.web3j.abi.datatypes.Address(160, token)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> lockedOfRequest(byte[] requestId) {
        final Function function = new Function(FUNC_LOCKEDOFREQUEST, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(requestId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> owner() {
        final Function function = new Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> paidCountOfRequest(byte[] requestId) {
        final Function function = new Function(FUNC_PAIDCOUNTOFREQUEST, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(requestId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint16>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> releaseEscrow(String spender, String token, BigInteger amount) {
        final Function function = new Function(
                FUNC_RELEASEESCROW, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, spender), 
                new org.web3j.abi.datatypes.Address(160, token), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> releaseForRequest(byte[] requestId) {
        final Function function = new Function(
                FUNC_RELEASEFORREQUEST, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(requestId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> renounceOwnership() {
        final Function function = new Function(
                FUNC_RENOUNCEOWNERSHIP, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> totalLockedFor(String token) {
        final Function function = new Function(FUNC_TOTALLOCKEDFOR, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, token)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> transferByRouter(String spender, List<Payment> payments) {
        final Function function = new Function(
                FUNC_TRANSFERBYROUTER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, spender), 
                new org.web3j.abi.datatypes.DynamicArray<Payment>(Payment.class, payments)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> transferOwnership(String newOwner) {
        final Function function = new Function(
                FUNC_TRANSFEROWNERSHIP, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, newOwner)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> typeAndVersion() {
        final Function function = new Function(FUNC_TYPEANDVERSION, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> withdraw(String token, BigInteger amount) {
        final Function function = new Function(
                FUNC_WITHDRAW, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, token), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static List<ApprovalEventResponse> getApprovalEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(APPROVAL_EVENT, transactionReceipt);
        ArrayList<ApprovalEventResponse> responses = new ArrayList<ApprovalEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ApprovalEventResponse typedResponse = new ApprovalEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.spender = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.token = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static ApprovalEventResponse getApprovalEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(APPROVAL_EVENT, log);
        ApprovalEventResponse typedResponse = new ApprovalEventResponse();
        typedResponse.log = log;
        typedResponse.spender = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.token = (String) eventValues.getIndexedValues().get(1).getValue();
        typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<ApprovalEventResponse> approvalEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getApprovalEventFromLog(log));
    }

    public Flowable<ApprovalEventResponse> approvalEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(APPROVAL_EVENT));
        return approvalEventFlowable(filter);
    }

    public static List<DepositEventResponse> getDepositEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(DEPOSIT_EVENT, transactionReceipt);
        ArrayList<DepositEventResponse> responses = new ArrayList<DepositEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            DepositEventResponse typedResponse = new DepositEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.token = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static DepositEventResponse getDepositEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(DEPOSIT_EVENT, log);
        DepositEventResponse typedResponse = new DepositEventResponse();
        typedResponse.log = log;
        typedResponse.token = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<DepositEventResponse> depositEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getDepositEventFromLog(log));
    }

    public Flowable<DepositEventResponse> depositEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(DEPOSIT_EVENT));
        return depositEventFlowable(filter);
    }

    public static List<EscrowEventResponse> getEscrowEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(ESCROW_EVENT, transactionReceipt);
        ArrayList<EscrowEventResponse> responses = new ArrayList<EscrowEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            EscrowEventResponse typedResponse = new EscrowEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.spender = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.token = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.locked = (Boolean) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static EscrowEventResponse getEscrowEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(ESCROW_EVENT, log);
        EscrowEventResponse typedResponse = new EscrowEventResponse();
        typedResponse.log = log;
        typedResponse.spender = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.token = (String) eventValues.getIndexedValues().get(1).getValue();
        typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.locked = (Boolean) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<EscrowEventResponse> escrowEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getEscrowEventFromLog(log));
    }

    public Flowable<EscrowEventResponse> escrowEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ESCROW_EVENT));
        return escrowEventFlowable(filter);
    }

    public static List<OwnershipTransferredEventResponse> getOwnershipTransferredEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, transactionReceipt);
        ArrayList<OwnershipTransferredEventResponse> responses = new ArrayList<OwnershipTransferredEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static OwnershipTransferredEventResponse getOwnershipTransferredEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, log);
        OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
        typedResponse.log = log;
        typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<OwnershipTransferredEventResponse> ownershipTransferredEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getOwnershipTransferredEventFromLog(log));
    }

    public Flowable<OwnershipTransferredEventResponse> ownershipTransferredEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(OWNERSHIPTRANSFERRED_EVENT));
        return ownershipTransferredEventFlowable(filter);
    }

    public static List<RequestDisbursedEventResponse> getRequestDisbursedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(REQUESTDISBURSED_EVENT, transactionReceipt);
        ArrayList<RequestDisbursedEventResponse> responses = new ArrayList<RequestDisbursedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RequestDisbursedEventResponse typedResponse = new RequestDisbursedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.requestId = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.token = (String) eventValues.getIndexedValues().get(2).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.paidCount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static RequestDisbursedEventResponse getRequestDisbursedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(REQUESTDISBURSED_EVENT, log);
        RequestDisbursedEventResponse typedResponse = new RequestDisbursedEventResponse();
        typedResponse.log = log;
        typedResponse.requestId = (byte[]) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
        typedResponse.token = (String) eventValues.getIndexedValues().get(2).getValue();
        typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.paidCount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<RequestDisbursedEventResponse> requestDisbursedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getRequestDisbursedEventFromLog(log));
    }

    public Flowable<RequestDisbursedEventResponse> requestDisbursedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(REQUESTDISBURSED_EVENT));
        return requestDisbursedEventFlowable(filter);
    }

    public static List<RequestLockedEventResponse> getRequestLockedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(REQUESTLOCKED_EVENT, transactionReceipt);
        ArrayList<RequestLockedEventResponse> responses = new ArrayList<RequestLockedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RequestLockedEventResponse typedResponse = new RequestLockedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.requestId = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.spender = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.token = (String) eventValues.getIndexedValues().get(2).getValue();
            typedResponse.totalAmount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.redundancy = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static RequestLockedEventResponse getRequestLockedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(REQUESTLOCKED_EVENT, log);
        RequestLockedEventResponse typedResponse = new RequestLockedEventResponse();
        typedResponse.log = log;
        typedResponse.requestId = (byte[]) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.spender = (String) eventValues.getIndexedValues().get(1).getValue();
        typedResponse.token = (String) eventValues.getIndexedValues().get(2).getValue();
        typedResponse.totalAmount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.redundancy = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<RequestLockedEventResponse> requestLockedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getRequestLockedEventFromLog(log));
    }

    public Flowable<RequestLockedEventResponse> requestLockedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(REQUESTLOCKED_EVENT));
        return requestLockedEventFlowable(filter);
    }

    public static List<RequestReleasedEventResponse> getRequestReleasedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(REQUESTRELEASED_EVENT, transactionReceipt);
        ArrayList<RequestReleasedEventResponse> responses = new ArrayList<RequestReleasedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RequestReleasedEventResponse typedResponse = new RequestReleasedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.requestId = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.spender = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.token = (String) eventValues.getIndexedValues().get(2).getValue();
            typedResponse.amountRefunded = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static RequestReleasedEventResponse getRequestReleasedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(REQUESTRELEASED_EVENT, log);
        RequestReleasedEventResponse typedResponse = new RequestReleasedEventResponse();
        typedResponse.log = log;
        typedResponse.requestId = (byte[]) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.spender = (String) eventValues.getIndexedValues().get(1).getValue();
        typedResponse.token = (String) eventValues.getIndexedValues().get(2).getValue();
        typedResponse.amountRefunded = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<RequestReleasedEventResponse> requestReleasedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getRequestReleasedEventFromLog(log));
    }

    public Flowable<RequestReleasedEventResponse> requestReleasedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(REQUESTRELEASED_EVENT));
        return requestReleasedEventFlowable(filter);
    }

    public static List<TransferEventResponse> getTransferEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(TRANSFER_EVENT, transactionReceipt);
        ArrayList<TransferEventResponse> responses = new ArrayList<TransferEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TransferEventResponse typedResponse = new TransferEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.spender = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.token = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static TransferEventResponse getTransferEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(TRANSFER_EVENT, log);
        TransferEventResponse typedResponse = new TransferEventResponse();
        typedResponse.log = log;
        typedResponse.spender = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
        typedResponse.token = (String) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<TransferEventResponse> transferEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getTransferEventFromLog(log));
    }

    public Flowable<TransferEventResponse> transferEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TRANSFER_EVENT));
        return transferEventFlowable(filter);
    }

    public static List<WithdrawEventResponse> getWithdrawEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(WITHDRAW_EVENT, transactionReceipt);
        ArrayList<WithdrawEventResponse> responses = new ArrayList<WithdrawEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            WithdrawEventResponse typedResponse = new WithdrawEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.token = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static WithdrawEventResponse getWithdrawEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(WITHDRAW_EVENT, log);
        WithdrawEventResponse typedResponse = new WithdrawEventResponse();
        typedResponse.log = log;
        typedResponse.token = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<WithdrawEventResponse> withdrawEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getWithdrawEventFromLog(log));
    }

    public Flowable<WithdrawEventResponse> withdrawEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(WITHDRAW_EVENT));
        return withdrawEventFlowable(filter);
    }

    @Deprecated
    public static Wallet load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Wallet(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Wallet load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Wallet(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Wallet load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new Wallet(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Wallet load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Wallet(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<Wallet> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, String router, String initialOwner) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, router), 
                new org.web3j.abi.datatypes.Address(160, initialOwner)));
        return deployRemoteCall(Wallet.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor);
    }

    public static RemoteCall<Wallet> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, String router, String initialOwner) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, router), 
                new org.web3j.abi.datatypes.Address(160, initialOwner)));
        return deployRemoteCall(Wallet.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<Wallet> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String router, String initialOwner) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, router), 
                new org.web3j.abi.datatypes.Address(160, initialOwner)));
        return deployRemoteCall(Wallet.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<Wallet> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String router, String initialOwner) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, router), 
                new org.web3j.abi.datatypes.Address(160, initialOwner)));
        return deployRemoteCall(Wallet.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static class Payment extends StaticStruct {
        public String recipient;

        public String feeToken;

        public BigInteger feeAmount;

        public Payment(String recipient, String feeToken, BigInteger feeAmount) {
            super(new org.web3j.abi.datatypes.Address(160, recipient), 
                    new org.web3j.abi.datatypes.Address(160, feeToken), 
                    new org.web3j.abi.datatypes.generated.Uint256(feeAmount));
            this.recipient = recipient;
            this.feeToken = feeToken;
            this.feeAmount = feeAmount;
        }

        public Payment(Address recipient, Address feeToken, Uint256 feeAmount) {
            super(recipient, feeToken, feeAmount);
            this.recipient = recipient.getValue();
            this.feeToken = feeToken.getValue();
            this.feeAmount = feeAmount.getValue();
        }
    }

    public static class ApprovalEventResponse extends BaseEventResponse {
        public String spender;

        public String token;

        public BigInteger amount;
    }

    public static class DepositEventResponse extends BaseEventResponse {
        public String token;

        public BigInteger amount;
    }

    public static class EscrowEventResponse extends BaseEventResponse {
        public String spender;

        public String token;

        public BigInteger amount;

        public Boolean locked;
    }

    public static class OwnershipTransferredEventResponse extends BaseEventResponse {
        public String previousOwner;

        public String newOwner;
    }

    public static class RequestDisbursedEventResponse extends BaseEventResponse {
        public byte[] requestId;

        public String to;

        public String token;

        public BigInteger amount;

        public BigInteger paidCount;
    }

    public static class RequestLockedEventResponse extends BaseEventResponse {
        public byte[] requestId;

        public String spender;

        public String token;

        public BigInteger totalAmount;

        public BigInteger redundancy;
    }

    public static class RequestReleasedEventResponse extends BaseEventResponse {
        public byte[] requestId;

        public String spender;

        public String token;

        public BigInteger amountRefunded;
    }

    public static class TransferEventResponse extends BaseEventResponse {
        public String spender;

        public String to;

        public String token;

        public BigInteger amount;
    }

    public static class WithdrawEventResponse extends BaseEventResponse {
        public String token;

        public BigInteger amount;
    }
}
