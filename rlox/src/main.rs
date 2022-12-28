mod virtual_machine;
mod errors;
mod values;

use bytes::Bytes;
use values::{OP_CONSTANT, OP_ADD, OP_MULTIPLY, Value};
use virtual_machine::VirtualMachine;
use errors::VirtualMachineError;

fn main() -> Result<(), VirtualMachineError> {
    let operations = Bytes::from_static(&[
        OP_CONSTANT,
        0,
        OP_CONSTANT,
        1,
        OP_ADD,
        OP_CONSTANT,
        1,
        OP_MULTIPLY,
    ]);
    let constants = vec![Value::Number(1.0), Value::Number(2.0)];
    let mut vm = VirtualMachine::new(operations, constants);
    vm.run()
}
