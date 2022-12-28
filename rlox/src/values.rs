type LoxNumber = f64;
type LoxString = String;

#[allow(dead_code)]
#[derive(Clone, Debug)]
pub enum Value {
    Number(LoxNumber),
    String(LoxString),
    Boolean(bool),
    Nil,
}

pub const OP_CONSTANT: u8 = 0;
pub const OP_ADD: u8 = 1;
pub const OP_SUBTRACT: u8 = 2;
pub const OP_MULTIPLY: u8 = 3;
pub const OP_DIVIDE: u8 = 4;
pub const OP_NEGATE: u8 = 5;
